import { fireEvent, screen, waitFor, within } from "@testing-library/react";
import { expect, test, vi } from "vitest";
import { i18n } from "@siard-web/shared";
import de from "@siard-web/shared/i18n/de.json";
import en from "@siard-web/shared/i18n/en.json";
import fr from "@siard-web/shared/i18n/fr.json";
import it from "@siard-web/shared/i18n/it.json";
import { renderAt } from "../test/render";
import { chooserRoutes, HOME, stubRoutes } from "../test/routes";

const plain = (value: string) => value.replace(/<\/?strong>/g, "");

test.each([
  { language: "en", resource: en },
  { language: "de", resource: de },
  { language: "fr", resource: fr },
  { language: "it", resource: it },
])(
  "$language shows the complete translated overview",
  async ({ language, resource }) => {
    await i18n.changeLanguage(language);
    renderAt("/");
    const text = resource.startPage;
    for (const action of ["archive", "open", "upload", "export"] as const) {
      expect(
        screen.getByRole("button", { name: text[action] }),
      ).toHaveAttribute("type", "button");
    }
    expect(
      screen.getByRole("heading", { level: 1, name: resource.welcome.text }),
    ).toBeInTheDocument();
    const overview = screen.getByRole("region", { name: "SIARD" });
    for (const description of [
      text.archiveDescription,
      text.openDescription,
      text.destinationDescription,
    ]) {
      expect(overview).toHaveTextContent(plain(description));
    }
    expect(overview.querySelectorAll("strong")).toHaveLength(4);
    expect(screen.queryByRole("img")).not.toBeInTheDocument();
  },
);

// Open left this loop for its own tests below (design D8)
test("query text is ignored and pointer and keyboard activation are inert", async () => {
  await i18n.changeLanguage("en");
  renderAt("/?title=%3Cscript%3Ealert(1)%3C%2Fscript%3E");
  const overview = screen.getByRole("region", { name: "SIARD" });
  const before = overview.innerHTML;
  expect(overview).not.toHaveTextContent("alert(1)");
  expect(overview.querySelector("script")).toBeNull();
  for (const name of [en.startPage.upload, en.startPage.export]) {
    const button = screen.getByRole("button", { name });
    fireEvent.click(button);
    for (const key of ["Enter", " "]) {
      fireEvent.keyDown(button, { key });
      fireEvent.keyUp(button, { key });
    }
  }
  expect(overview.innerHTML).toBe(before);
  expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
  expect(
    screen.queryByRole("heading", { name: /SIARD Suite 2/ }),
  ).not.toBeInTheDocument();
});

test("archive button navigates to the connection picker", async () => {
  await i18n.changeLanguage("en");
  // both picker queries answer with empty data
  vi.stubGlobal(
    "fetch",
    vi.fn(
      async () =>
        new Response("[]", {
          status: 200,
          headers: { "Content-Type": "application/json" },
        }),
    ),
  );
  renderAt("/");

  const archive = screen.getByRole("button", { name: en.startPage.archive });
  expect(archive).toHaveAttribute("type", "button");
  fireEvent.click(archive);

  expect(
    await screen.findByRole("heading", { name: en.archive.title }),
  ).toBeInTheDocument();
  expect(
    screen.queryByRole("region", { name: "SIARD" }),
  ).not.toBeInTheDocument();
});

test("archive activation adds no launch token or query to the route or requests", async () => {
  await i18n.changeLanguage("en");
  const fetchMock = vi.fn(
    async () =>
      new Response("[]", {
        status: 200,
        headers: { "Content-Type": "application/json" },
      }),
  );
  vi.stubGlobal("fetch", fetchMock);
  renderAt("/");

  fireEvent.click(screen.getByRole("button", { name: en.startPage.archive }));
  await screen.findByRole("heading", { name: en.archive.title });

  expect(window.location.search).toBe("");
  expect(fetchMock).toHaveBeenCalled();
  for (const [input, init] of fetchMock.mock.calls as unknown as [
    RequestInfo | URL,
    RequestInit | undefined,
  ][]) {
    const request =
      input instanceof Request ? input : new Request(String(input), init);
    const url = new URL(request.url);
    expect(url.search).toBe("");
    expect(url.href).not.toMatch(/token/i);
    expect(request.method).toBe("GET");
  }
});

// Spec requirement "Open workflow entry point" of local-web-start-page

test("Open opens the chooser in open mode offering .siard files", async () => {
  await i18n.changeLanguage("en");
  const requests = stubRoutes(chooserRoutes);
  renderAt("/");

  fireEvent.click(screen.getByRole("button", { name: en.startPage.open }));

  const dialog = await screen.findByRole("dialog", {
    name: en.fileChooser.titleOpen,
  });
  expect(
    within(await within(dialog).findByRole("list", { name: HOME })).getByText(
      "db.siard",
    ),
  ).toBeInTheDocument();
  const listings = requests.filter(
    (request) => request.url.pathname === "/api/files",
  );
  expect(listings[0].url.searchParams.get("path")).toBe(HOME);
  expect(listings[0].url.searchParams.getAll("extension")).toEqual([".siard"]);
  expect(listings[0].url.searchParams.get("foldersOnly")).toBe("false");
});

test("Open is a native button, so Enter and Space activate it like a click", async () => {
  await i18n.changeLanguage("en");
  renderAt("/");

  // jsdom does not turn Enter or Space into a click; browsers do for a native button
  const open = screen.getByRole("button", { name: en.startPage.open });
  expect(open.tagName).toBe("BUTTON");
  expect(open).toHaveAttribute("type", "button");
  expect(open).not.toHaveAttribute("tabindex", "-1");
  expect(open).toBeEnabled();
});

test("cancelling the chooser stays on the start page and returns focus to Open", async () => {
  await i18n.changeLanguage("en");
  const requests = stubRoutes(chooserRoutes);
  renderAt("/");
  const open = screen.getByRole("button", { name: en.startPage.open });
  open.focus();

  fireEvent.click(open);
  const dialog = await screen.findByRole("dialog", {
    name: en.fileChooser.titleOpen,
  });
  await within(dialog).findByRole("list", { name: HOME });
  fireEvent.click(
    within(dialog).getByRole("button", { name: en.fileChooser.cancel }),
  );

  await waitFor(() =>
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument(),
  );
  expect(open).toHaveFocus();
  expect(screen.getByRole("region", { name: "SIARD" })).toBeInTheDocument();
  expect(requests.some((request) => request.method === "POST")).toBe(false);
});

test("Open without a session shows the chooser's error and no entries", async () => {
  await i18n.changeLanguage("en");
  const requests = stubRoutes({
    "GET /api/files/places": [401, { status: 401 }],
  });
  renderAt("/");

  fireEvent.click(screen.getByRole("button", { name: en.startPage.open }));

  const dialog = await screen.findByRole("dialog", {
    name: en.fileChooser.titleOpen,
  });
  expect(
    await within(dialog).findByText(en.session.missing),
  ).toBeInTheDocument();
  expect(within(dialog).queryByText("db.siard")).not.toBeInTheDocument();
  expect(window.location.search).toBe("");
  for (const request of requests) {
    expect(request.url.href).not.toMatch(/token/i);
    expect(request.body).not.toMatch(/token/i);
  }
});
