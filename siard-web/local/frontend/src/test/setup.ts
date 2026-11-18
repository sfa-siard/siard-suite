import "@siard-web/shared/test/setup";
import { beforeEach, vi } from "vitest";

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
