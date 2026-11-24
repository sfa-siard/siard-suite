import { vi } from "vitest";

/** Answers every API request with the given status and JSON body; returns the mock. */
/** A JSON response, or a problem response for error statuses. */
export function jsonResponse(status: number, body?: unknown) {
  return new Response(body === undefined ? null : JSON.stringify(body), {
    status,
    headers: {
      "Content-Type":
        status < 300 ? "application/json" : "application/problem+json",
    },
  });
}

export function respondWith(status: number, body?: unknown) {
  const fetch = vi.fn(async () => jsonResponse(status, body));
  vi.stubGlobal("fetch", fetch);
  return fetch;
}
