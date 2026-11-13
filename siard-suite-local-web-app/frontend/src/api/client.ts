import createFetchClient from "openapi-fetch";
import createClient from "openapi-react-query";
import type { paths } from "./schema";

/** A non-2xx response; the problem details body is not needed by callers so far. */
export class ApiError extends Error {
  readonly status: number;

  constructor(status: number) {
    super(`HTTP ${status}`);
    this.name = "ApiError";
    this.status = status;
  }
}

export const fetchClient = createFetchClient<paths>({
  // same origin as the page; absolute so it also works outside a browser (tests)
  baseUrl: globalThis.location?.origin ?? "",
  // looked up per call, so tests can replace the global fetch
  fetch: (request) => globalThis.fetch(request),
});

// every non-2xx response becomes an ApiError, so views can tell 401 apart from other failures
fetchClient.use({
  onResponse({ response }) {
    if (!response.ok) {
      throw new ApiError(response.status);
    }
  },
});

/**
 * Whether a query error is an HTTP 401: the session is gone. Query errors are typed from the
 * documented error responses (none so far), so they are checked as unknown here.
 */
export function isUnauthorized(error: unknown): boolean {
  return error instanceof ApiError && error.status === 401;
}

/** Typed TanStack Query hooks, e.g. `$api.useQuery("get", "/api/system/info")`. */
export const $api = createClient(fetchClient);
