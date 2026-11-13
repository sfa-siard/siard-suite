import { screen } from "@testing-library/react";
import { expect, test } from "vitest";
import en from "../i18n/en.json";
import { renderAt, respondWith, systemInfo } from "../test/render";

// Spec requirement "About view"

test("About view shows the version", async () => {
  respondWith(200, systemInfo);

  renderAt("/about");

  expect(
    await screen.findByRole("heading", { name: "SIARD Suite 2.2.168" }),
  ).toBeInTheDocument();
  expect(screen.getByText(en.info.dialog.text)).toBeInTheDocument();
});

test("information cannot be loaded", async () => {
  respondWith(500, { status: 500, title: "Internal Server Error" });

  renderAt("/about");

  expect(await screen.findByRole("alert")).toHaveTextContent(
    en.about.loadError,
  );
  expect(screen.queryByText(/SIARD Suite \d/)).not.toBeInTheDocument();
});

test("no session", async () => {
  respondWith(401, { status: 401, title: "Unauthorized" });

  renderAt("/about");

  expect(await screen.findByRole("alert")).toHaveTextContent(
    en.session.missing,
  );
  expect(screen.queryByText(/SIARD Suite \d/)).not.toBeInTheDocument();
});
