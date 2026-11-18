import "@testing-library/jest-dom/vitest";
import { cleanup } from "@testing-library/react";
import { afterEach, vi } from "vitest";
import i18n from "../i18n";

// test setup every web app's frontend tests share; an app adds its own stubs in its setup file
afterEach(async () => {
  cleanup();
  vi.unstubAllGlobals();
  vi.restoreAllMocks();
  await i18n.changeLanguage("en");
});
