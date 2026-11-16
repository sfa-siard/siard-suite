import { screen } from "@testing-library/react";
import { expect, test } from "vitest";
import i18n, { startLanguage } from "../i18n";
import en from "../i18n/en.json";
import { renderAt, respondWith } from "../test/render";

// Spec requirement "Rejected launch link message". "Token not visible after rejection" is the
// agent's redirect to exactly /launch-failed, tested in the backend's LaunchResourceTest.

test("expired link in the browser", () => {
  const fetch = respondWith(200, {});

  renderAt("/launch-failed");

  expect(screen.getByRole("alert")).toHaveTextContent(en.launchFailed.text);
  expect(screen.queryByText(/SIARD Suite \d/)).not.toBeInTheDocument();
  expect(fetch).not.toHaveBeenCalled();
});

test("message in an unsupported browser language", async () => {
  await i18n.changeLanguage(
    startLanguage({ languages: ["es"], language: "es" }),
  );

  renderAt("/launch-failed");

  expect(screen.getByRole("alert")).toHaveTextContent(en.launchFailed.text);
});
