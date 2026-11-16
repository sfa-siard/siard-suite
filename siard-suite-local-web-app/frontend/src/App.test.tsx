import { screen } from "@testing-library/react";
import { expect, test } from "vitest";
import en from "./i18n/en.json";
import { renderAt, respondWith, systemInfo } from "./test/render";

// the three routes of the app shell
test("renders the start, about and launch-failed routes", async () => {
  respondWith(200, systemInfo);

  const start = renderAt("/");
  expect(
    screen.getByRole("heading", { name: en.welcome.text }),
  ).toBeInTheDocument();
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

  renderAt("/launch-failed");
  expect(screen.getByRole("alert")).toHaveTextContent(en.launchFailed.text);
});
