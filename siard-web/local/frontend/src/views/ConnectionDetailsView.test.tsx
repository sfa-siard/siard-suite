import { fireEvent, screen, waitFor } from "@testing-library/react";
import { expect, test, vi } from "vitest";
import en from "@siard-web/shared/i18n/en.json";
import { renderAt } from "../test/render";

// Spec requirement "Connection details page"

const catalog = [
  {
    id: "postgresql",
    name: "PostgreSQL",
    kind: "server",
    fields: [
      { key: "host", type: "text", required: true },
      { key: "port", type: "text", required: true },
      { key: "dbName", type: "text", required: true },
      { key: "user", type: "text", required: true },
      { key: "password", type: "password", required: false },
      { key: "options", type: "text", required: false },
    ],
    examples: {
      host: "db.exampleHost",
      port: "5432",
      dbName: "exampleDb",
    },
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

function respondApi(routes: Routes) {
  const fetchMock = vi.fn(
    async (input: RequestInfo | URL, init?: RequestInit) => {
      const request =
        input instanceof Request ? input : new Request(String(input), init);
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
    },
  );
  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

const okApi = {
  "GET /api/dbms": { body: catalog },
  "GET /api/connections": { body: [archiveA] },
};

function fill(label: string | RegExp, value: string) {
  fireEvent.change(screen.getByLabelText(label), { target: { value } });
}

function postCalls(fetchMock: ReturnType<typeof vi.fn>) {
  return fetchMock.mock.calls.filter(([input]) => {
    const request =
      input instanceof Request ? input : new Request(String(input));
    return (
      request.method === "POST" &&
      new URL(request.url).pathname === "/api/connections"
    );
  });
}

test("new connection form shows placeholders and no saved data", async () => {
  respondApi(okApi);

  renderAt("/archive/connection?dbms=postgresql");

  const host = await screen.findByLabelText(/^Host/);
  expect(host).toHaveAttribute("placeholder", "db.exampleHost");
  expect(host).toHaveValue("");
  expect(screen.getByLabelText(/^Port/)).toHaveAttribute("placeholder", "5432");
  expect(screen.getByLabelText(/^Database \*/)).toHaveAttribute(
    "placeholder",
    "exampleDb",
  );
  expect(screen.getByLabelText(/^Password/)).toHaveAttribute(
    "type",
    "password",
  );
  expect(screen.getByLabelText(/^Connection name/)).toHaveValue("");
});

test("saved connection is prefilled with an empty password and fixed DBMS", async () => {
  respondApi(okApi);

  renderAt("/archive/connection?connection=7");

  expect(await screen.findByLabelText(/^Host/)).toHaveValue("db.example.org");
  expect(screen.getByLabelText(/^Port/)).toHaveValue("5432");
  expect(screen.getByLabelText(/^Database \*/)).toHaveValue("archive");
  expect(screen.getByLabelText(/^User/)).toHaveValue("archivist");
  expect(screen.getByLabelText(/^Options/)).toHaveValue("ssl=true");
  expect(screen.getByLabelText(/^Password/)).toHaveValue("");
  expect(screen.getByLabelText(/^Database system/)).toBeDisabled();
  expect(screen.getByLabelText(/^Connection name/)).toHaveValue("Archive A");
});

test("validation blocks submit with inline messages and no request", async () => {
  const fetchMock = respondApi(okApi);

  renderAt("/archive/connection?dbms=postgresql");
  await screen.findByLabelText(/^Host/);

  fireEvent.click(screen.getByRole("button", { name: en.connections.save }));

  expect(await screen.findAllByText(en.connections.required)).not.toHaveLength(
    0,
  );
  expect(postCalls(fetchMock)).toHaveLength(0);

  fill(/^Host/, "db.example.org");
  fill(/^Port/, "abc");
  fill(/^Database \*/, "archive");
  fill(/^User/, "archivist");
  fill(/^Connection name/, "Archive B");
  fireEvent.click(screen.getByRole("button", { name: en.connections.save }));

  expect(
    await screen.findByText(en.connections.invalidPort),
  ).toBeInTheDocument();
  expect(postCalls(fetchMock)).toHaveLength(0);
});

test("save sends the fields without the password and returns to /archive", async () => {
  const fetchMock = respondApi({
    ...okApi,
    "POST /api/connections": { status: 201, body: archiveA },
  });

  renderAt("/archive/connection?dbms=postgresql");
  await screen.findByLabelText(/^Host/);

  fill(/^Connection name/, "Archive B");
  fill(/^Host/, "db.example.org");
  fill(/^Port/, "5432");
  fill(/^Database \*/, "archive");
  fill(/^User/, "archivist");
  fill(/^Options/, "ssl=true");
  fill(/^Password/, "s3cret!");
  fireEvent.click(
    await screen.findByRole("button", { name: en.connections.save }),
  );

  await waitFor(() => expect(postCalls(fetchMock)).toHaveLength(1));
  const request = postCalls(fetchMock)[0][0] as Request;
  const body = await request.clone().json();
  expect(body).toEqual({
    name: "Archive B",
    dbmsId: "postgresql",
    host: "db.example.org",
    port: "5432",
    dbName: "archive",
    user: "archivist",
    options: "ssl=true",
  });
  expect(JSON.stringify(body)).not.toContain("s3cret");
  expect(body).not.toHaveProperty("password");

  expect(
    await screen.findByRole("heading", { name: en.archive.title }),
  ).toBeInTheDocument();
});

test("submit without a name does not save and returns to /archive", async () => {
  const fetchMock = respondApi(okApi);

  renderAt("/archive/connection?dbms=postgresql");
  await screen.findByLabelText(/^Host/);

  fill(/^Host/, "db.example.org");
  fill(/^Port/, "5432");
  fill(/^Database \*/, "archive");
  fill(/^User/, "archivist");
  fireEvent.click(screen.getByRole("button", { name: en.connections.save }));

  expect(
    await screen.findByRole("heading", { name: en.archive.title }),
  ).toBeInTheDocument();
  expect(postCalls(fetchMock)).toHaveLength(0);
});

test("unknown dbms and connection identifiers show an error with a way back", async () => {
  respondApi(okApi);

  const { unmount } = renderAt("/archive/connection?dbms=unknown");
  expect(await screen.findByRole("alert")).toHaveTextContent(
    en.connections.unknown,
  );
  expect(
    screen.getByRole("link", { name: en.connections.backToArchive }),
  ).toHaveAttribute("href", "/archive");
  unmount();

  renderAt("/archive/connection?connection=999");
  expect(await screen.findByRole("alert")).toHaveTextContent(
    en.connections.unknown,
  );
});

test("missing parameters show the unknown-identifier error", async () => {
  respondApi(okApi);

  renderAt("/archive/connection");

  expect(await screen.findByRole("alert")).toHaveTextContent(
    en.connections.unknown,
  );
});

test("catalog API failure shows a localized error", async () => {
  respondApi({
    "GET /api/dbms": { status: 500, body: { title: "Error" } },
    "GET /api/connections": { body: [archiveA] },
  });

  renderAt("/archive/connection?dbms=postgresql");

  expect(await screen.findByRole("alert")).toHaveTextContent(
    en.connections.loadError,
  );
});
