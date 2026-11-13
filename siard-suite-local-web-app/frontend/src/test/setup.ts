import "@testing-library/jest-dom/vitest";
import { cleanup } from "@testing-library/react";
import { afterEach, beforeEach, vi } from "vitest";
import i18n from "../i18n";

// jsdom has no EventSource; the app shell opens one for presence
class FakeEventSource {
  onmessage: ((message: MessageEvent<string>) => void) | null = null;
  readonly url: string;

  constructor(url: string) {
    this.url = url;
  }

  close() {}
}

beforeEach(() => {
  vi.stubGlobal("EventSource", FakeEventSource);
});

afterEach(async () => {
  cleanup();
  vi.unstubAllGlobals();
  vi.restoreAllMocks();
  await i18n.changeLanguage("en");
});
