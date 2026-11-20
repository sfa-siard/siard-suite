import { screen } from "@testing-library/react";
import { expect, test, vi } from "vitest";
import en from "@siard-web/shared/i18n/en.json";
import { renderAt, systemInfo } from "./test/render";

// the routes of the app shell
test("renders the start, about, archive and launch-failed routes", async () => {
  vi.stubGlobal(
    "fetch",
    vi.fn(async (input: RequestInfo | URL) => {
      const url = input instanceof Request ? input.url : String(input);
      const body = url.includes("/api/system/info") ? systemInfo : [];
      return new Response(JSON.stringify(body), {
        status: 200,
        headers: { "Content-Type": "application/json" },
      });
    }),
  );

  const start = renderAt("/");
  expect(screen.getByRole("region", { name: "SIARD" })).toBeInTheDocument();
  expect(screen.getByRole("link", { name: en.menu.item.info })).toHaveAttribute(
    "href",
    "/about",
  );
  start.unmount();

  const about = renderAt("/about");
  expect(
    await screen.findByRole("heading", { name: "SIARD Suite 2.2.168" }),
  ).toBeInTheDocument();
  about.unmount();

  const archive = renderAt("/archive");
  expect(
    await screen.findByRole("heading", { name: en.archive.title }),
  ).toBeInTheDocument();
  archive.unmount();

  renderAt("/launch-failed");
  expect(screen.getByRole("alert")).toHaveTextContent(en.launchFailed.text);
});
