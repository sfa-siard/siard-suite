import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import { vi } from "vitest";
import App from "../App";

/** Renders the whole app at the given path, as after a navigation or reload. */
export function renderAt(path: string) {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[path]}>
        <App />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

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

export const systemInfo = {
  appVersion: "2.2.168",
  javaVersion: "21.0.2",
  jdbcDrivers: ["org.postgresql.Driver"],
};
