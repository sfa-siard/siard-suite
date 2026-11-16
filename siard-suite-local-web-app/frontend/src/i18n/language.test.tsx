import { cleanup, screen } from "@testing-library/react";
import { expect, test, vi } from "vitest";
import { renderAt, respondWith, systemInfo } from "../test/render";
import en from "./en.json";
import fr from "./fr.json";
import i18n, { startLanguage } from ".";

// Spec requirement "Interface language": on load the frontend picks startLanguage(navigator)

type Browser = Pick<Navigator, "languages" | "language">;

async function loadAboutWith(browser: Browser) {
  await i18n.changeLanguage(startLanguage(browser));
  respondWith(200, systemInfo);
  renderAt("/about");
  await screen.findByRole("heading", { name: "SIARD Suite 2.2.168" });
}

// the About description shows the language
test("supported browser language", async () => {
  await loadAboutWith({ languages: ["fr-CH", "de"], language: "fr-CH" });

  expect(screen.getByText(fr.info.dialog.text)).toBeInTheDocument();
});

test("unsupported browser language", async () => {
  await loadAboutWith({ languages: ["es"], language: "es" });

  expect(screen.getByText(en.info.dialog.text)).toBeInTheDocument();
});

test("malformed language value", async () => {
  const consoleError = vi.spyOn(console, "error");
  const malformed = [
    { languages: [""], language: "" },
    { languages: [], language: "" },
    { languages: ["-", "%%"], language: "-" },
    { languages: undefined, language: undefined },
  ] as unknown as Browser[];

  for (const browser of malformed) {
    await loadAboutWith(browser);
    expect(screen.getByText(en.info.dialog.text)).toBeInTheDocument();
    cleanup();
  }

  expect(consoleError).not.toHaveBeenCalled();
});
