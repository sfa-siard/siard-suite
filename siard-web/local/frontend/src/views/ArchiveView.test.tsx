import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import {
  fireEvent,
  render,
  screen,
  waitFor,
  within,
} from "@testing-library/react";
import { MemoryRouter, Route, Routes, useLocation } from "react-router";
import { expect, test, vi } from "vitest";
import en from "@siard-web/shared/i18n/en.json";
import ArchiveView from "./ArchiveView";
import { renderAt } from "../test/render";

// Spec requirement "Connection picker page"

const field = (key: string, type = "text", required = true) => ({
  key,
  type,
  required,
});

const catalog = [
  {
    id: "postgresql",
    name: "PostgreSQL",
    kind: "server",
    fields: [
      field("host"),
      field("port"),
      field("dbName"),
      field("user"),
      field("password", "password", false),
      field("options", "text", false),
    ],
    examples: { host: "db.exampleHost", port: "5432", dbName: "archive" },
  },
  {
    id: "mysql",
    name: "MySQL",
    kind: "server",
    fields: [field("host"), field("port"), field("dbName"), field("user")],
    examples: {},
  },
  {
    id: "mssql",
    name: "Microsoft SQL Server",
    kind: "server",
    fields: [field("host"), field("port"), field("dbName"), field("user")],
    examples: {},
  },
  {
    id: "oracle",
    name: "Oracle",
    kind: "server",
    fields: [field("host"), field("port"), field("dbName"), field("user")],
    examples: {},
  },
  {
    id: "db2",
    name: "DB/2",
    kind: "server",
    fields: [field("host"), field("port"), field("dbName"), field("user")],
    examples: {},
  },
];

const archiveA = {
  id: 7,
  name: "Archive A",
  dbmsId: "postgresql",
  host: "db.example.org",
  port: "5432",
  dbName: "archive",
  user: "archivist",
  options: "ssl=true",
  file: null,
  createdAt: "2026-01-01T00:00:00Z",
  updatedAt: "2026-01-01T00:00:00Z",
};

type Handler = { status?: number; body?: unknown };
type Routes = Record<string, Handler | (() => Handler)>;

/** Answers each `METHOD /api/path` request from the given table; 404 otherwise. */
function respondApi(routes: Routes) {
  const fetchMock = vi.fn(async (input: RequestInfo | URL) => {
    const request =
      input instanceof Request ? input : new Request(String(input));
    const path = new URL(request.url).pathname;
    const handler = routes[`${request.method} ${path}`];
    const resolved =
      typeof handler === "function" ? handler() : (handler ?? {});
    const status = resolved.status ?? (handler ? 200 : 404);
    const body = resolved.body ?? { title: "Not Found" };
    return new Response(JSON.stringify(body), {
      status,
      headers: {
        "Content-Type":
          status < 300 ? "application/json" : "application/problem+json",
      },
    });
  });
  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

const ok = {
  "GET /api/connections": { body: [] as unknown[] },
  "GET /api/dbms": { body: catalog },
};

function DetailsProbe() {
  const location = useLocation();
  return <div data-testid="details-probe">{location.search}</div>;
}

/** Renders the picker with a probe on the details route to observe navigation. */
function renderPicker(path = "/archive") {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[path]}>
        <Routes>
          <Route path="/archive" element={<ArchiveView />} />
          <Route path="/archive/connection" element={<DetailsProbe />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

test("saved connections and the DBMS catalog render", async () => {
  respondApi({
    "GET /api/connections": { body: [archiveA] },
    "GET /api/dbms": { body: catalog },
  });

  renderAt("/archive");

  expect(
    await screen.findByRole("heading", { name: en.archive.title }),
  ).toBeInTheDocument();
  expect(await screen.findByText("Archive A")).toBeInTheDocument();
  expect(screen.getByText("db.example.org:5432/archive")).toBeInTheDocument();
  expect(
    screen.getByRole("heading", { name: en.archive.savedConnections }),
  ).toBeInTheDocument();
  expect(
    screen.getByRole("heading", { name: en.archive.newConnection }),
  ).toBeInTheDocument();
  for (const entry of catalog) {
    expect(
      screen.getByRole("button", { name: entry.name }),
    ).toBeInTheDocument();
  }
});

test("selecting a DBMS navigates with the dbms query parameter", async () => {
  respondApi(ok);

  renderPicker();

  fireEvent.click(await screen.findByRole("button", { name: "PostgreSQL" }));

  expect(await screen.findByTestId("details-probe")).toHaveTextContent(
    "?dbms=postgresql",
  );
});

test("selecting a saved connection navigates with the connection parameter", async () => {
  respondApi({
    "GET /api/connections": { body: [archiveA] },
    "GET /api/dbms": { body: catalog },
  });

  renderPicker();

  fireEvent.click(await screen.findByRole("button", { name: /^Archive A/ }));

  expect(await screen.findByTestId("details-probe")).toHaveTextContent(
    "?connection=7",
  );
});

test("delete asks for confirmation; cancel sends nothing", async () => {
  respondApi({
    "GET /api/connections": { body: [archiveA] },
    "GET /api/dbms": { body: catalog },
  });
  const fetchMock = vi.mocked(globalThis.fetch);

  renderAt("/archive");

  fireEvent.click(
    await screen.findByRole("button", {
      name: en.archive.delete.replace("{{name}}", "Archive A"),
    }),
  );
  expect(
    await screen.findByRole("dialog", { name: en.archive.deleteTitle }),
  ).toBeInTheDocument();

  fireEvent.click(
    screen.getByRole("button", { name: en.archive.deleteCancel }),
  );

  await waitFor(() =>
    expect(
      screen.queryByRole("dialog", { name: en.archive.deleteTitle }),
    ).not.toBeInTheDocument(),
  );
  expect(fetchMock).not.toHaveBeenCalledWith(
    expect.objectContaining({ method: "DELETE" }) as never,
  );
});

test("delete confirmation sends DELETE and refreshes the list", async () => {
  let deleted = false;
  const fetchMock = respondApi({
    "GET /api/connections": () => ({
      body: deleted ? [] : [archiveA],
    }),
    "GET /api/dbms": { body: catalog },
    "DELETE /api/connections/7": () => {
      deleted = true;
      return { status: 204 };
    },
  });

  renderAt("/archive");

  fireEvent.click(
    await screen.findByRole("button", {
      name: en.archive.delete.replace("{{name}}", "Archive A"),
    }),
  );
  fireEvent.click(
    await screen.findByRole("button", { name: en.archive.deleteConfirm }),
  );

  await waitFor(() =>
    expect(
      screen.queryByRole("button", { name: /Archive A/ }),
    ).not.toBeInTheDocument(),
  );
  const deleteCall = fetchMock.mock.calls.find(
    ([input]) =>
      input instanceof Request &&
      input.method === "DELETE" &&
      new URL(input.url).pathname === "/api/connections/7",
  );
  expect(deleteCall).toBeDefined();
});

test("a failed delete shows its error inside the open dialog", async () => {
  respondApi({
    "GET /api/connections": { body: [archiveA] },
    "GET /api/dbms": { body: catalog },
    "DELETE /api/connections/7": { status: 500, body: { title: "Error" } },
  });

  renderAt("/archive");

  fireEvent.click(
    await screen.findByRole("button", {
      name: en.archive.delete.replace("{{name}}", "Archive A"),
    }),
  );
  fireEvent.click(
    await screen.findByRole("button", { name: en.archive.deleteConfirm }),
  );

  const dialog = screen.getByRole("dialog");
  expect(
    await within(dialog).findByText(en.archive.deleteError),
  ).toBeInTheDocument();
});

test("empty state when no connections are saved", async () => {
  respondApi(ok);

  renderAt("/archive");

  expect(await screen.findByText(en.archive.empty)).toBeInTheDocument();
  for (const entry of catalog) {
    expect(
      screen.getByRole("button", { name: entry.name }),
    ).toBeInTheDocument();
  }
});

test("untrusted query text is not rendered", async () => {
  respondApi(ok);

  renderAt("/archive?name=%3Cscript%3Ealert(1)%3C%2Fscript%3E");

  await screen.findByText(en.archive.empty);
  expect(screen.queryByText(/alert\(1\)/)).not.toBeInTheDocument();
  expect(document.querySelector("script")).toBeNull();
});

test("connections API failure shows a localized error", async () => {
  respondApi({
    "GET /api/connections": { status: 500, body: { title: "Error" } },
    "GET /api/dbms": { body: catalog },
  });

  renderAt("/archive");

  expect(await screen.findByRole("alert")).toHaveTextContent(
    en.archive.loadError,
  );
  // the catalog still renders
  expect(
    screen.getByRole("button", { name: "PostgreSQL" }),
  ).toBeInTheDocument();
});
