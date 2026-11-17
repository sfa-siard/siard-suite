import { vi } from "vitest";

/** Answers every API request with the given status and JSON body; returns the mock. */
export function respondWith(status: number, body?: unknown) {
  const fetch = vi.fn(
    async () =>
      new Response(body === undefined ? null : JSON.stringify(body), {
        status,
        headers: {
          "Content-Type":
            status < 300 ? "application/json" : "application/problem+json",
        },
      }),
  );
  vi.stubGlobal("fetch", fetch);
  return fetch;
}
