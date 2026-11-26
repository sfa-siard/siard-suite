import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import {
  act,
  fireEvent,
  render,
  screen,
  waitFor,
  within,
} from "@testing-library/react";
import { describe, expect, test, vi } from "vitest";
import { jsonResponse } from "@siard-web/shared/test";
import { i18n } from "@siard-web/shared";
import en from "@siard-web/shared/i18n/en.json";
import FileChooserDialog, {
  type FileChooserOptions,
  join,
  withExtension,
} from "./FileChooserDialog";
import { useFileChooser } from "./useFileChooser";

// Spec requirements "Chooser modes", "Chooser navigation", "Path field" and "Show in file
// browser from the chooser" of local-web-file-chooser, against a fake agent behind fetch.

interface FakeEntry {
  name: string;
  folder?: boolean;
  hidden?: boolean;
  size?: number;
}

interface FakeAgent {
  places?: { kind: string; path: string }[] | number;
  /** folder path -> entries, or an error status */
  folders: Record<string, FakeEntry[] | number>;
  /** extra paths for the entry lookup that are not in a listed folder */
  writable?: Record<string, boolean>;
  truncated?: string[];
  reveal?: number;
}

const HOME = "/home/me";

function parentOf(path: string) {
  const separator = path.includes("\\") && !path.includes("/") ? "\\" : "/";
  const index = path.lastIndexOf(separator);
  if (index <= 0) {
    return separator === "/" && path !== "/" ? "/" : null;
  }
  const parent = path.slice(0, index);
  return /^[A-Za-z]:$/.test(parent) ? parent + "\\" : parent;
}

function baseName(path: string) {
  return path.split(/[\\/]/).pop() ?? path;
}

/** Stubs fetch with a fake agent; returns the requests it received. */
function stubAgent(agent: FakeAgent) {
  const requests: { method: string; url: URL; body?: unknown }[] = [];
  const entry = (path: string) => {
    const parent = parentOf(path);
    if (Object.hasOwn(agent.folders, path)) {
      return { path, name: baseName(path), parent, folder: true };
    }
    const siblings = parent === null ? undefined : agent.folders[parent];
    const found = Array.isArray(siblings)
      ? siblings.find((sibling) => sibling.name === baseName(path))
      : undefined;
    return found
      ? { path, name: found.name, parent, folder: found.folder ?? false }
      : null;
  };
  vi.stubGlobal(
    "fetch",
    vi.fn(async (request: Request) => {
      const url = new URL(request.url);
      const body =
        request.method === "POST" ? await request.clone().json() : undefined;
      requests.push({ method: request.method, url, body });
      const query = url.searchParams;
      switch (url.pathname) {
        case "/api/files/places": {
          const places = agent.places ?? [{ kind: "home", path: HOME }];
          return typeof places === "number"
            ? jsonResponse(places, { status: places })
            : jsonResponse(200, places);
        }
        case "/api/files": {
          const path = query.get("path") ?? "";
          const content = agent.folders[path];
          if (content === undefined) {
            return jsonResponse(404, { status: 404 });
          }
          if (typeof content === "number") {
            return jsonResponse(content, {
              status: content,
              detail: "anything",
            });
          }
          const extensions = query.getAll("extension");
          const entries = content
            .filter((item) => query.get("hidden") === "true" || !item.hidden)
            .filter(
              (item) => item.folder || query.get("foldersOnly") !== "true",
            )
            .filter(
              (item) =>
                item.folder ||
                extensions.length === 0 ||
                extensions.some((extension) => item.name.endsWith(extension)),
            )
            .map((item) => ({
              name: item.name,
              path: join(path, item.name),
              folder: item.folder ?? false,
              size: item.folder ? null : (item.size ?? 1),
              modified: "2026-11-25T10:00:00Z",
              hidden: item.hidden ?? false,
            }));
          return jsonResponse(200, {
            path,
            parent: parentOf(path),
            entries,
            truncated: agent.truncated?.includes(path) ?? false,
          });
        }
        case "/api/files/entry": {
          const path = query.get("path") ?? "";
          const found = entry(path);
          if (!found) {
            return jsonResponse(404, { status: 404 });
          }
          return jsonResponse(200, {
            ...found,
            size: found.folder ? null : 1,
            modified: "2026-11-25T10:00:00Z",
            hidden: false,
            writable: agent.writable?.[path] ?? true,
          });
        }
        case "/api/files/reveal": {
          const status = agent.reveal ?? 204;
          return status === 204
            ? new Response(null, { status: 204 })
            : jsonResponse(status, { status });
        }
        default:
          return jsonResponse(404, { status: 404 });
      }
    }),
  );
  return requests;
}

function renderChooser(options: FileChooserOptions) {
  const onClose = vi.fn();
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  render(
    <QueryClientProvider client={queryClient}>
      <FileChooserDialog {...options} onClose={onClose} />
    </QueryClientProvider>,
  );
  return onClose;
}

const listing = (path: string) => screen.findByRole("list", { name: path });
const confirmButton = (name: string) => screen.getByRole("button", { name });

/** The MUI severity of the alert that shows `text`. */
async function severityOf(text: string) {
  const alert = (await screen.findByText(text)).closest(".MuiAlert-root");
  return /colorWarning/.test(alert?.className ?? "") ? "warning" : "error";
}

async function pastePath(text: string) {
  const field = screen.getByRole("textbox", { name: en.fileChooser.path });
  fireEvent.change(field, { target: { value: text } });
  fireEvent.keyDown(field, { key: "Enter" });
}

const homeTree = {
  [HOME]: [
    { name: "Documents", folder: true },
    { name: "a.siard" },
    { name: "b.txt" },
  ],
  [`${HOME}/Documents`]: [{ name: "2024", folder: true }],
  [`${HOME}/Documents/2024`]: [{ name: "old.siard" }],
};

describe("Chooser navigation", () => {
  test("opens in home, browses places and subfolders and goes up", async () => {
    stubAgent({
      places: [
        { kind: "home", path: HOME },
        { kind: "documents", path: `${HOME}/Documents` },
        { kind: "root", path: "/" },
      ],
      folders: homeTree,
    });
    renderChooser({ mode: "open", extensions: [".siard"] });

    expect(await listing(HOME)).toBeInTheDocument();
    const places = screen.getByRole("list", { name: en.fileChooser.places });
    expect(
      within(places).getByText(en.fileChooser.place.home),
    ).toBeInTheDocument();
    expect(within(places).getByText("/")).toBeInTheDocument();

    fireEvent.click(within(places).getByText(en.fileChooser.place.documents));
    fireEvent.doubleClick(
      within(await listing(`${HOME}/Documents`)).getByText("2024"),
    );
    expect(
      within(await listing(`${HOME}/Documents/2024`)).getByText("old.siard"),
    ).toBeInTheDocument();

    fireEvent.click(
      screen.getByRole("button", { name: en.fileChooser.parent }),
    );
    expect(await listing(`${HOME}/Documents`)).toBeInTheDocument();
  });

  test("Enter on a folder opens it", async () => {
    stubAgent({ folders: homeTree });
    renderChooser({ mode: "open", extensions: [".siard"] });

    fireEvent.keyDown(within(await listing(HOME)).getByText("Documents"), {
      key: "Enter",
    });

    expect(await listing(`${HOME}/Documents`)).toBeInTheDocument();
  });

  test("an unreadable folder shows an error naming it and the chooser stays usable", async () => {
    stubAgent({
      folders: { ...homeTree, [`${HOME}/Documents`]: 403 },
    });
    renderChooser({ mode: "open", extensions: [".siard"] });

    fireEvent.doubleClick(within(await listing(HOME)).getByText("Documents"));

    expect(
      await screen.findByText(
        `The folder "${HOME}/Documents" may not be read.`,
      ),
    ).toBeInTheDocument();
    fireEvent.click(
      within(
        screen.getByRole("list", { name: en.fileChooser.places }),
      ).getByText(en.fileChooser.place.home),
    );
    expect(await listing(HOME)).toBeInTheDocument();
  });

  test.each([
    [504, en.fileChooser.error.unreachable],
    [503, en.fileChooser.error.busy],
  ])(
    "status %i decides the message, whatever the detail says",
    async (status, text) => {
      stubAgent({ folders: { ...homeTree, [`${HOME}/Documents`]: status } });
      renderChooser({ mode: "open", extensions: [".siard"] });

      fireEvent.doubleClick(within(await listing(HOME)).getByText("Documents"));

      expect(await screen.findByText(text)).toBeInTheDocument();
      expect(
        screen.getByRole("list", { name: en.fileChooser.places }),
      ).toBeInTheDocument();
    },
  );

  test("a truncated listing is announced, a complete one is not", async () => {
    stubAgent({ folders: homeTree, truncated: [`${HOME}/Documents`] });
    renderChooser({ mode: "open", extensions: [".siard"] });

    await listing(HOME);
    expect(screen.queryByRole("alert")).not.toBeInTheDocument();

    fireEvent.doubleClick(within(await listing(HOME)).getByText("Documents"));

    expect(
      await screen.findByText(i18n.t("fileChooser.truncated", { count: 1 })),
    ).toBeInTheDocument();
  });

  test("hidden entries are requested only when shown", async () => {
    const requests = stubAgent({
      folders: { [HOME]: [{ name: ".cache", folder: true, hidden: true }] },
    });
    renderChooser({ mode: "open", extensions: [".siard"] });

    await listing(HOME);
    expect(screen.queryByText(".cache")).not.toBeInTheDocument();
    fireEvent.click(screen.getByLabelText(en.fileChooser.showHidden));

    expect(await screen.findByText(".cache")).toBeInTheDocument();
    expect(
      requests.some(
        (request) =>
          request.url.pathname === "/api/files" &&
          request.url.searchParams.get("hidden") === "true",
      ),
    ).toBe(true);
  });

  test("without a session the chooser shows a warning and no entries", async () => {
    stubAgent({ places: 401, folders: homeTree });
    const onClose = renderChooser({ mode: "open", extensions: [".siard"] });

    expect(await severityOf(en.session.missing)).toBe("warning");
    expect(screen.queryByText("a.siard")).not.toBeInTheDocument();
    expect(confirmButton(en.fileChooser.open)).toBeDisabled();
    expect(onClose).not.toHaveBeenCalled();
  });
});

describe("Path field", () => {
  test.each([
    [
      "Windows Copy as path",
      '"C:\\Users\\me\\My Data\\db.siard"',
      "C:\\Users\\me\\My Data",
      "C:\\Users\\me\\My Data\\db.siard",
    ],
    [
      "macOS path",
      "/Users/me/My Data/db.siard",
      "/Users/me/My Data",
      "/Users/me/My Data/db.siard",
    ],
    [
      "Linux URI",
      "file:///home/me/My%20Data/db.siard",
      "/home/me/My Data",
      "/home/me/My Data/db.siard",
    ],
  ])(
    "pasting a file selects it in its folder (%s)",
    async (_name, pasted, folder, file) => {
      stubAgent({ folders: { [HOME]: [], [folder]: [{ name: "db.siard" }] } });
      const onClose = renderChooser({ mode: "open", extensions: [".siard"] });
      await listing(HOME);

      await pastePath(pasted);

      const shown = await listing(folder);
      expect(
        within(shown)
          .getByText("db.siard")
          .closest("[role=button], .Mui-selected"),
      ).toHaveClass("Mui-selected");
      expect(confirmButton(en.fileChooser.open)).toBeEnabled();
      fireEvent.click(confirmButton(en.fileChooser.open));
      expect(onClose).toHaveBeenCalledWith(file);
    },
  );

  test("pasting a quoted folder shows that folder", async () => {
    const folder = "C:\\Users\\me\\My Data";
    stubAgent({ folders: { [HOME]: [], [folder]: [{ name: "x.siard" }] } });
    renderChooser({ mode: "open", extensions: [".siard"] });
    await listing(HOME);

    await pastePath(`"${folder}"`);

    expect(
      within(await listing(folder)).getByText("x.siard"),
    ).toBeInTheDocument();
  });

  test("a pasted file far down the list is scrolled into view and named (next to the buttons)", async () => {
    const many = Array.from({ length: 50 }, (_, index) => ({
      name: `${String(index).padStart(2, "0")}.siard`,
    }));
    stubAgent({ folders: { [HOME]: many } });
    // jsdom has no scrollIntoView
    const scrolled = vi.fn();
    Element.prototype.scrollIntoView = scrolled;
    try {
      renderChooser({ mode: "open", extensions: [".siard"] });
      await listing(HOME);

      await pastePath(`${HOME}/49.siard`);

      expect(
        await screen.findByText(
          i18n.t("fileChooser.selected", { name: "49.siard" }),
        ),
      ).toBeInTheDocument();
      const row = within(await listing(HOME))
        .getByText("49.siard")
        .closest(".Mui-selected");
      expect(scrolled.mock.contexts).toContain(row);
    } finally {
      delete (Element.prototype as Partial<Element>).scrollIntoView;
    }
  });

  test("a pasted hidden file is named next to the buttons although it is not listed", async () => {
    stubAgent({
      folders: {
        [HOME]: [{ name: "a.siard" }, { name: ".secret.siard", hidden: true }],
      },
    });
    const onClose = renderChooser({ mode: "open", extensions: [".siard"] });
    await listing(HOME);

    await pastePath(`${HOME}/.secret.siard`);

    expect(
      await screen.findByText(
        i18n.t("fileChooser.selected", { name: ".secret.siard" }),
      ),
    ).toBeInTheDocument();
    expect(
      within(await listing(HOME)).queryByText(".secret.siard"),
    ).not.toBeInTheDocument();
    fireEvent.click(confirmButton(en.fileChooser.open));
    expect(onClose).toHaveBeenCalledWith(`${HOME}/.secret.siard`);
  });

  test("a missing path and a file of the wrong type show errors and return nothing", async () => {
    stubAgent({ folders: { [HOME]: [{ name: "notes.txt" }] } });
    const onClose = renderChooser({ mode: "open", extensions: [".siard"] });
    await listing(HOME);

    await pastePath(`${HOME}/missing.siard`);
    expect(
      await screen.findByText(en.fileChooser.error.notFound),
    ).toBeInTheDocument();

    await pastePath(`${HOME}/notes.txt`);
    expect(
      await screen.findByText(
        i18n.t("fileChooser.error.wrongType", { extensions: ".siard" }),
      ),
    ).toBeInTheDocument();
    expect(confirmButton(en.fileChooser.open)).toBeDisabled();
    expect(onClose).not.toHaveBeenCalled();
  });

  test.each([["../secret"], [""]])(
    "malformed input %j shows an error and sends no request with it",
    async (input) => {
      const requests = stubAgent({ folders: homeTree });
      renderChooser({ mode: "open", extensions: [".siard"] });
      await listing(HOME);
      const before = requests.length;

      await pastePath(input);

      expect(
        await screen.findByText(en.fileChooser.error.notAbsolute),
      ).toBeInTheDocument();
      expect(requests.length).toBe(before);
      expect(
        requests.some((request) => request.url.href.includes("secret")),
      ).toBe(false);
    },
  );
});

describe("Chooser modes", () => {
  test("open returns the selected archive and lists no other files", async () => {
    const requests = stubAgent({ folders: homeTree });
    const onClose = renderChooser({ mode: "open", extensions: [".siard"] });

    const shown = await listing(HOME);
    expect(within(shown).queryByText("b.txt")).not.toBeInTheDocument();
    expect(confirmButton(en.fileChooser.open)).toBeDisabled();
    fireEvent.click(within(shown).getByText("a.siard"));
    fireEvent.click(confirmButton(en.fileChooser.open));

    expect(onClose).toHaveBeenCalledWith(`${HOME}/a.siard`);
    expect(
      requests
        .find((request) => request.url.pathname === "/api/files")
        ?.url.searchParams.getAll("extension"),
    ).toEqual([".siard"]);
  });

  test("double-clicking a matching file opens it", async () => {
    stubAgent({ folders: homeTree });
    const onClose = renderChooser({ mode: "open", extensions: [".siard"] });

    fireEvent.doubleClick(within(await listing(HOME)).getByText("a.siard"));

    expect(onClose).toHaveBeenCalledWith(`${HOME}/a.siard`);
  });

  test("save appends the extension and asks before replacing an existing file", async () => {
    stubAgent({ folders: { [HOME]: [{ name: "db.siard" }] } });
    const onClose = renderChooser({ mode: "save", extension: ".siard" });
    await listing(HOME);

    fireEvent.change(
      screen.getByRole("textbox", { name: en.fileChooser.name }),
      {
        target: { value: "db" },
      },
    );
    fireEvent.click(confirmButton(en.fileChooser.save));

    const dialog = await screen.findByRole("dialog", {
      name: en.fileChooser.replaceTitle,
    });
    expect(
      within(dialog).getByText(
        '"db.siard" already exists. Do you want to replace it?',
      ),
    ).toBeInTheDocument();
    expect(onClose).not.toHaveBeenCalled();
    fireEvent.click(
      within(dialog).getByRole("button", {
        name: en.fileChooser.replaceConfirm,
      }),
    );
    expect(onClose).toHaveBeenCalledWith(`${HOME}/db.siard`);
  });

  test("save of a new name returns its path without asking", async () => {
    stubAgent({ folders: { [HOME]: [] } });
    const onClose = renderChooser({
      mode: "save",
      extension: ".siard",
      initialName: "archive",
    });
    await listing(HOME);

    fireEvent.click(confirmButton(en.fileChooser.save));

    await waitFor(() =>
      expect(onClose).toHaveBeenCalledWith(`${HOME}/archive.siard`),
    );
    expect(
      screen.queryByRole("dialog", { name: en.fileChooser.replaceTitle }),
    ).not.toBeInTheDocument();
  });

  test("save with the name of an existing folder opens that folder", async () => {
    const requests = stubAgent({
      folders: {
        [HOME]: [{ name: "Reports", folder: true }],
        [`${HOME}/Reports`]: [{ name: "old.siard" }],
      },
    });
    const onClose = renderChooser({ mode: "save", extension: ".siard" });
    await listing(HOME);
    const name = screen.getByRole("textbox", { name: en.fileChooser.name });

    fireEvent.change(name, { target: { value: "Reports" } });
    fireEvent.click(confirmButton(en.fileChooser.save));

    expect(
      within(await listing(`${HOME}/Reports`)).getByText("old.siard"),
    ).toBeInTheDocument();
    expect(name).toHaveValue("");
    expect(onClose).not.toHaveBeenCalled();
    expect(
      screen.queryByRole("dialog", { name: en.fileChooser.replaceTitle }),
    ).not.toBeInTheDocument();
    // decided on the name as typed, before any extension is appended
    expect(
      requests.some((request) =>
        request.url.searchParams.get("path")?.endsWith("Reports.siard"),
      ),
    ).toBe(false);
  });

  test("save where the name with the extension is a folder shows an error and stays", async () => {
    stubAgent({
      folders: {
        [HOME]: [{ name: "Archives.siard", folder: true }],
        [`${HOME}/Archives.siard`]: [{ name: "inner.siard" }],
      },
    });
    const onClose = renderChooser({ mode: "save", extension: ".siard" });
    await listing(HOME);
    const name = screen.getByRole("textbox", { name: en.fileChooser.name });

    fireEvent.change(name, { target: { value: "Archives" } });
    fireEvent.click(confirmButton(en.fileChooser.save));

    expect(
      await screen.findByText(
        i18n.t("fileChooser.error.folderExists", { name: "Archives.siard" }),
      ),
    ).toBeInTheDocument();
    expect(await listing(HOME)).toBeInTheDocument();
    expect(screen.queryByText("inner.siard")).not.toBeInTheDocument();
    expect(name).toHaveValue("Archives");
    expect(onClose).not.toHaveBeenCalled();
    expect(
      screen.queryByRole("dialog", { name: en.fileChooser.replaceTitle }),
    ).not.toBeInTheDocument();
  });

  test("save into a folder that is not writable shows an error and returns nothing", async () => {
    stubAgent({ folders: { [HOME]: [] }, writable: { [HOME]: false } });
    const onClose = renderChooser({
      mode: "save",
      extension: ".siard",
      initialName: "db",
    });
    await listing(HOME);

    fireEvent.click(confirmButton(en.fileChooser.save));

    expect(
      await screen.findByText(en.fileChooser.error.notWritable),
    ).toBeInTheDocument();
    expect(onClose).not.toHaveBeenCalled();
  });

  test("folder mode lists folders only and returns the folder shown", async () => {
    const requests = stubAgent({ folders: homeTree });
    const onClose = renderChooser({ mode: "folder" });

    const shown = await listing(HOME);
    expect(within(shown).queryByText("a.siard")).not.toBeInTheDocument();
    fireEvent.doubleClick(within(shown).getByText("Documents"));
    await listing(`${HOME}/Documents`);
    fireEvent.click(confirmButton(en.fileChooser.chooseFolder));

    expect(onClose).toHaveBeenCalledWith(`${HOME}/Documents`);
    expect(
      requests
        .filter((request) => request.url.pathname === "/api/files")
        .every(
          (request) => request.url.searchParams.get("foldersOnly") === "true",
        ),
    ).toBe(true);
  });

  test("cancel returns nothing and issues no further request", async () => {
    const requests = stubAgent({ folders: homeTree });
    const onClose = renderChooser({ mode: "open", extensions: [".siard"] });
    await listing(HOME);
    const before = requests.length;

    fireEvent.click(
      screen.getByRole("button", { name: en.fileChooser.cancel }),
    );

    expect(onClose).toHaveBeenCalledWith(null);
    expect(requests.length).toBe(before);
  });
});

describe("Show in file browser from the chooser", () => {
  test("reveals the selection, or the current folder when nothing is selected", async () => {
    const requests = stubAgent({ folders: homeTree });
    renderChooser({ mode: "open", extensions: [".siard"] });
    const shown = await listing(HOME);
    const reveals = () =>
      requests
        .filter((request) => request.url.pathname === "/api/files/reveal")
        .map((request) => request.body);

    fireEvent.click(
      screen.getByRole("button", { name: en.fileChooser.reveal }),
    );
    await waitFor(() => expect(reveals()).toEqual([{ path: HOME }]));

    fireEvent.click(within(shown).getByText("a.siard"));
    fireEvent.click(
      screen.getByRole("button", { name: en.fileChooser.reveal }),
    );
    await waitFor(() =>
      expect(reveals()).toEqual([{ path: HOME }, { path: `${HOME}/a.siard` }]),
    );
  });

  test.each([
    [503, en.fileChooser.error.revealFailed, "error"],
    [401, en.session.missing, "warning"],
  ])(
    "a %i answer shows a message and the chooser stays open",
    async (status, text, severity) => {
      stubAgent({ folders: homeTree, reveal: status });
      const onClose = renderChooser({ mode: "open", extensions: [".siard"] });
      fireEvent.click(within(await listing(HOME)).getByText("a.siard"));

      fireEvent.click(
        screen.getByRole("button", { name: en.fileChooser.reveal }),
      );

      expect(await severityOf(text)).toBe(severity);
      expect(
        screen.getByRole("dialog", { name: en.fileChooser.titleOpen }),
      ).toBeInTheDocument();
      expect(onClose).not.toHaveBeenCalled();
    },
  );
});

describe("helpers", () => {
  test("the save extension is appended unless the name already ends with it", () => {
    expect(withExtension("db", ".siard")).toBe("db.siard");
    expect(withExtension("db.SIARD", ".siard")).toBe("db.SIARD");
    // another extension is part of the name, not a format choice
    expect(withExtension("db.zip", ".siard")).toBe("db.zip.siard");
  });

  test("names are joined with the folder's own separator", () => {
    expect(join("/home/me", "db.siard")).toBe("/home/me/db.siard");
    expect(join("/", "db.siard")).toBe("/db.siard");
    expect(join("C:\\Users\\me", "db.siard")).toBe("C:\\Users\\me\\db.siard");
    expect(join("C:\\", "db.siard")).toBe("C:\\db.siard");
  });
});

test("useFileChooser resolves with the chosen path, or null when cancelled", async () => {
  stubAgent({ folders: homeTree });
  const results: (string | null)[] = [];
  function Workflow() {
    const { choose, chooser } = useFileChooser();
    return (
      <>
        <button
          onClick={async () =>
            results.push(await choose({ mode: "open", extensions: [".siard"] }))
          }
        >
          start
        </button>
        {chooser}
      </>
    );
  }
  render(
    <QueryClientProvider
      client={
        new QueryClient({ defaultOptions: { queries: { retry: false } } })
      }
    >
      <Workflow />
    </QueryClientProvider>,
  );

  fireEvent.click(screen.getByText("start"));
  fireEvent.doubleClick(within(await listing(HOME)).getByText("a.siard"));
  await waitFor(() => expect(results).toEqual([`${HOME}/a.siard`]));
  expect(screen.queryByRole("dialog")).not.toBeInTheDocument();

  fireEvent.click(screen.getByText("start"));
  await listing(HOME);
  await act(async () => {
    fireEvent.click(
      screen.getByRole("button", { name: en.fileChooser.cancel }),
    );
  });
  await waitFor(() => expect(results).toEqual([`${HOME}/a.siard`, null]));
});
