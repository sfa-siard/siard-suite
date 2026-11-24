import { fireEvent, screen } from "@testing-library/react";
import { expect, test, vi } from "vitest";
import { i18n } from "@siard-web/shared";
import de from "@siard-web/shared/i18n/de.json";
import en from "@siard-web/shared/i18n/en.json";
import fr from "@siard-web/shared/i18n/fr.json";
import it from "@siard-web/shared/i18n/it.json";
import { renderAt } from "../test/render";

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

test("query text is ignored and pointer and keyboard activation are inert", async () => {
  await i18n.changeLanguage("en");
  renderAt("/?title=%3Cscript%3Ealert(1)%3C%2Fscript%3E");
  const overview = screen.getByRole("region", { name: "SIARD" });
  const before = overview.innerHTML;
  expect(overview).not.toHaveTextContent("alert(1)");
  expect(overview.querySelector("script")).toBeNull();
  for (const name of ["Open", "Upload", "Export"]) {
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

  const archive = screen.getByRole("button", { name: "Archive" });
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

  fireEvent.click(screen.getByRole("button", { name: "Archive" }));
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
