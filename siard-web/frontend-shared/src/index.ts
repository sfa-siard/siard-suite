// App-agnostic frontend code shared by the SIARD web apps (design R7). Importing it also
// initialises i18next with the four languages and types its keys.
import "./i18n/i18next";
export { theme } from "./theme";
export { default as i18n, startLanguage, supportedLanguages } from "./i18n";
export type { Language } from "./i18n";
export { ApiError, createApiClient, isUnauthorized } from "./api/client";
export { default as startPageArchive } from "./assets/start-page/archive.png";
export { default as startPageArchiveRed } from "./assets/start-page/archive_red.png";
export { default as startPageDatabase } from "./assets/start-page/siard-db.png";
export { default as startPageDatabaseRed } from "./assets/start-page/siard-db_red.png";
export { default as startPageExport } from "./assets/start-page/export.png";
export { default as startPageExportRed } from "./assets/start-page/export_red.png";
export { default as startPageArrow } from "./assets/start-page/arrow.png";
export { ArchiveBrowser } from "./archive/ArchiveBrowser";
export type { ArchiveBrowserProps } from "./archive/ArchiveBrowser";
