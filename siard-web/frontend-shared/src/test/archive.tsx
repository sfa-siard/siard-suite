import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router";
import { vi } from "vitest";
import { ArchiveBrowser } from "../archive/ArchiveBrowser";

export const ARCHIVE_ID = "a1";
const BASE = `/api/archives/${ARCHIVE_ID}`;

/** Answers API requests by path; a handler returns [status, body], or undefined for 404. */
export type Handler = (
  path: string,
  query: URLSearchParams,
) => [number, unknown] | undefined;

/** Stubs fetch with the handler and returns the paths and queries of all requests made. */
export function mockApi(handler: Handler) {
  const requests: URL[] = [];
  vi.stubGlobal(
    "fetch",
    vi.fn(async (request: Request) => {
      const url = new URL(request.url);
      requests.push(url);
      const [status, body] = handler(url.pathname, url.searchParams) ?? [
        404,
        undefined,
      ];
      return new Response(body === undefined ? null : JSON.stringify(body), {
        status,
        headers: {
          "Content-Type":
            status < 300 ? "application/json" : "application/problem+json",
        },
      });
    }),
  );
  return requests;
}

/** Renders the browser mounted as an app would, at the given path below the mount point. */
export function renderBrowser(below = "", onUnauthorized = () => {}) {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[`/archives/${ARCHIVE_ID}${below}`]}>
        <Routes>
          <Route
            path="archives/:archiveId/*"
            element={
              <ArchiveBrowser
                archiveId={ARCHIVE_ID}
                onUnauthorized={onUnauthorized}
              />
            }
          />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

/** The detail of the schema's only view. */
export const VIEW1 = {
  index: 0,
  name: "VIEW1",
  query: "SELECT CINTEGER, CVARCHAR FROM TSIMPLE",
  columns: [
    { index: 0, name: "CINTEGER", type: "INTEGER" },
    { index: 1, name: "CVARCHAR", type: "VARCHAR(256)" },
  ],
};

const text = (value: string) => ({ type: "text", value, truncated: false });

/**
 * A handler for one schema with the given tables; rows are generated as `r<row>` per cell, and
 * search requests are answered by the optional `search` handler.
 */
export function archiveHandler(
  tables: { name: string; rows: number; columns: string[] }[],
  search?: (query: URLSearchParams) => unknown,
): Handler {
  return (path, query) => {
    if (search && path.endsWith("/search")) {
      return [200, search(query)];
    }
    if (path === BASE) {
      return [
        200,
        {
          dbName: "Sample Database",
          schemas: [{ index: 0, name: "SampleSchema" }],
        },
      ];
    }
    if (path === `${BASE}/schemas/0`) {
      return [
        200,
        {
          index: 0,
          name: "SampleSchema",
          tables: tables.map((table, index) => ({
            index,
            name: table.name,
            rows: table.rows,
          })),
          views: [{ index: 0, name: "VIEW1" }],
        },
      ];
    }
    if (path === `${BASE}/schemas/0/views/0`) {
      return [200, VIEW1];
    }
    const match =
      /^\/api\/archives\/a1\/schemas\/0\/tables\/(\d+)(\/rows)?$/.exec(path);
    const table = match ? tables[Number(match[1])] : undefined;
    if (!match || !table) {
      return undefined;
    }
    if (!match[2]) {
      return [
        200,
        {
          index: Number(match[1]),
          name: table.name,
          rows: table.rows,
          columns: table.columns.map((name, index) => ({
            index,
            name,
            type: "VARCHAR(256)",
          })),
        },
      ];
    }
    const offset = Number(query.get("offset"));
    const limit = Number(query.get("limit"));
    const rows = [];
    for (let row = offset; row < Math.min(offset + limit, table.rows); row++) {
      rows.push({ row, cells: table.columns.map(() => text(`r${row}`)) });
    }
    return [200, { total: table.rows, offset, rows }];
  };
}
