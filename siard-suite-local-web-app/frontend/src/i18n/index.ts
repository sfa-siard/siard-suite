import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import de from "./de.json";
import en from "./en.json";
import fr from "./fr.json";
import it from "./it.json";

export const supportedLanguages = ["de", "fr", "it", "en"] as const;
export type Language = (typeof supportedLanguages)[number];

// typed against en.json: a key missing in another language fails the type check
const resources: Record<Language, { translation: typeof en }> = {
  de: { translation: de },
  fr: { translation: fr },
  it: { translation: it },
  en: { translation: en },
};

/**
 * The browser's first preferred language that the frontend supports, otherwise English.
 * Taken on every start and never stored (design D12): the origin changes with every port,
 * and the desktop app does not remember the language either.
 */
export function startLanguage(
  browser: Pick<Navigator, "languages" | "language"> = navigator,
): Language {
  const preferred =
    Array.isArray(browser.languages) && browser.languages.length > 0
      ? browser.languages
      : [browser.language];
  for (const tag of preferred) {
    if (typeof tag !== "string") {
      continue;
    }
    const primary = tag.split("-")[0].trim().toLowerCase();
    const match = supportedLanguages.find((language) => language === primary);
    if (match) {
      return match;
    }
  }
  return "en";
}

void i18n.use(initReactI18next).init({
  resources,
  lng: startLanguage(),
  fallbackLng: "en",
  interpolation: { escapeValue: false },
});

export default i18n;
