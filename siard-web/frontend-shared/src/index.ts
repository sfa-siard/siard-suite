// App-agnostic frontend code shared by the SIARD web apps (design R7). Importing it also
// initialises i18next with the four languages and types its keys.
import "./i18n/i18next";
export { theme } from "./theme";
export { default as i18n, startLanguage, supportedLanguages } from "./i18n";
export type { Language } from "./i18n";
export { ApiError, createApiClient, isUnauthorized } from "./api/client";
