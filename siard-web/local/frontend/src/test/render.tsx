import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import App from "../App";

export { respondWith } from "@siard-web/shared/test";

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

export const systemInfo = {
  appVersion: "2.2.168",
  javaVersion: "21.0.2",
  jdbcDrivers: ["org.postgresql.Driver"],
};
