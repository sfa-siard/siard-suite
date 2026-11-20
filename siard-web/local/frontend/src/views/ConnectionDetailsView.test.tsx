import { fireEvent, screen, waitFor, within } from "@testing-library/react";
import { expect, test, vi } from "vitest";
import en from "@siard-web/shared/i18n/en.json";
import { renderAt } from "../test/render";

// Spec requirement "Connection details page"

const serverFields = [
  { key: "host", type: "text", required: true },
  { key: "port", type: "text", required: true },
  { key: "dbName", type: "text", required: true },
  { key: "user", type: "text", required: true },
  { key: "password", type: "password", required: false },
  { key: "options", type: "text", required: false },
];

const catalogEntry = (id: string, name: string, port: string) => ({
  id,
  name,
  kind: "server",
  fields: serverFields,
  examples: {
    host: `${id}.exampleHost`,
    port,
    dbName: "exampleDb",
  },
});

const catalog = [
  catalogEntry("postgresql", "PostgreSQL", "5432"),
  catalogEntry("mysql", "MySQL", "3306"),
  catalogEntry("sqlserver", "Microsoft SQL Server", "1433"),
  catalogEntry("oracle", "Oracle", "1521"),
  catalogEntry("db2", "DB/2", "50000"),
];

const archiveA = {
  id: 7,
  name: "Archive A",
  dbmsId: "postgresql",
  host: "db.example.org",
  port: "15432",
  dbName: "archive",
  user: "archivist",
  options: "",
  file: null,
  createdAt: "2026-01-01T00:00:00Z",
  updatedAt: "2026-01-01T00:00:00Z",
};

type Handler = { status?: number; body?: unknown };
type Routes = Record<string, Handler | (() => Handler | Promise<Handler>)>;

function respondApi(routes: Routes) {
  const fetchMock = vi.fn(
    async (input: RequestInfo | URL, init?: RequestInit) => {
      const request =
        input instanceof Request ? input : new Request(String(input), init);
      const path = new URL(request.url).pathname;
      const handler = routes[`${request.method} ${path}`];
      const resolved =
        typeof handler === "function" ? await handler() : (handler ?? {});
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

function callsTo(
  fetchMock: ReturnType<typeof vi.fn>,
  method: string,
  path: string,
) {
  return fetchMock.mock.calls.filter(([input]) => {
    const request =
      input instanceof Request ? input : new Request(String(input));
    return request.method === method && new URL(request.url).pathname === path;
  });
}

function postCalls(fetchMock: ReturnType<typeof vi.fn>) {
  return callsTo(fetchMock, "POST", "/api/connections");
}

function putCalls(fetchMock: ReturnType<typeof vi.fn>) {
  return callsTo(fetchMock, "PUT", "/api/connections/7");
}

function clickSave() {
  fireEvent.click(
    within(screen.getByTestId("wizard-actions")).getByRole("button", {
      name: en.connections.save,
    }),
  );
}

test.each([
  ["postgresql", "PostgreSQL", "5432"],
  ["mysql", "MySQL", "3306"],
  ["sqlserver", "Microsoft SQL Server", "1433"],
  ["oracle", "Oracle", "1521"],
  ["db2", "DB/2", "50000"],
])(
  "new %s form defaults to localhost and the DBMS port as editable values",
  async (id, dbmsName, port) => {
    respondApi(okApi);

    renderAt(`/archive/connection?dbms=${id}`);

    const host = await screen.findByLabelText(/^Host/);
    expect(host).toHaveValue("localhost");
    expect(host).toBeEnabled();
    const portInput = screen.getByLabelText(/^Port/);
    expect(portInput).toHaveValue(port);
    expect(portInput).toBeEnabled();
    expect(screen.getByLabelText(/^Database \*/)).toHaveValue("");
    expect(screen.getByLabelText(/^User/)).toHaveValue("");
    expect(screen.getByLabelText(/^Password/)).toHaveValue("");
    expect(screen.getByLabelText(/^Password/)).toHaveAttribute(
      "type",
      "password",
    );
    expect(screen.getByLabelText(/^Options/)).toHaveValue("");
    expect(screen.getByLabelText(/^Connection name/)).toHaveValue("");
    expect(
      screen.getByRole("heading", { name: new RegExp(dbmsName) }),
    ).toBeInTheDocument();
    expect(screen.queryByLabelText(/database system/i)).not.toBeInTheDocument();
    expect(screen.queryByRole("checkbox")).not.toBeInTheDocument();
  },
);

test("user edits to the defaults are not replaced when other fields change", async () => {
  respondApi(okApi);

  renderAt("/archive/connection?dbms=postgresql");
  const host = await screen.findByLabelText(/^Host/);

  fill(/^Host/, "db.example.org");
  fill(/^Port/, "15432");
  fill(/^Database \*/, "archive");

  expect(host).toHaveValue("db.example.org");
  expect(screen.getByLabelText(/^Port/)).toHaveValue("15432");
});

test("a horizontal divider separates the details from the optional name", async () => {
  respondApi(okApi);
  renderAt("/archive/connection?dbms=postgresql");
  const name = await screen.findByLabelText(/^Connection name/);
  const divider = screen.getByRole("separator");
  expect(
    screen.getByLabelText(/^Options/).compareDocumentPosition(divider),
  ).toBe(Node.DOCUMENT_POSITION_FOLLOWING);
  expect(divider.compareDocumentPosition(name)).toBe(
    Node.DOCUMENT_POSITION_FOLLOWING,
  );
  expect(
    within(name.closest("form")!).queryByRole("group"),
  ).not.toBeInTheDocument();
});

test("the optional connection name is the last input", async () => {
  respondApi(okApi);

  renderAt("/archive/connection?dbms=postgresql");
  const name = await screen.findByLabelText(/^Connection name/);
  const options = screen.getByLabelText(/^Options/);

  expect(options.compareDocumentPosition(name)).toBe(
    Node.DOCUMENT_POSITION_FOLLOWING,
  );
});

test("saved connection is prefilled with an empty password and fixed DBMS", async () => {
  respondApi(okApi);

  renderAt("/archive/connection?connection=7");

  expect(await screen.findByLabelText(/^Host/)).toHaveValue("db.example.org");
  expect(screen.getByLabelText(/^Port/)).toHaveValue("15432");
  expect(screen.getByLabelText(/^Database \*/)).toHaveValue("archive");
  expect(screen.getByLabelText(/^User/)).toHaveValue("archivist");
  expect(screen.getByLabelText(/^Options/)).toHaveValue("");
  expect(screen.getByLabelText(/^Password/)).toHaveValue("");
  expect(screen.queryByLabelText(/database system/i)).not.toBeInTheDocument();
  expect(
    screen.getByRole("heading", { name: /PostgreSQL/ }),
  ).toBeInTheDocument();
  expect(screen.getByLabelText(/^Connection name/)).toHaveValue("Archive A");
});

test("eye button reveals and masks the password without submitting", async () => {
  const fetchMock = respondApi(okApi);
  renderAt("/archive/connection?dbms=postgresql");
  const password = await screen.findByLabelText(/^Password/);
  fill(/^Password/, "test-secret");
  expect(password).toHaveAttribute("type", "password");
  fireEvent.click(
    screen.getByRole("button", { name: en.connections.showPassword }),
  );
  expect(password).toHaveAttribute("type", "text");
  expect(password).toHaveValue("test-secret");
  fireEvent.click(
    screen.getByRole("button", { name: en.connections.hidePassword }),
  );
  expect(password).toHaveAttribute("type", "password");
  expect(password).toHaveValue("test-secret");
  expect(postCalls(fetchMock)).toHaveLength(0);
});

test("validation blocks submit with inline messages and no request", async () => {
  const fetchMock = respondApi(okApi);

  renderAt("/archive/connection?dbms=postgresql");
  await screen.findByLabelText(/^Host/);

  fireEvent.click(
    within(screen.getByTestId("wizard-actions")).getByRole("button", {
      name: en.connections.save,
    }),
  );

  expect(await screen.findAllByText(en.connections.required)).not.toHaveLength(
    0,
  );
  expect(postCalls(fetchMock)).toHaveLength(0);

  fill(/^Host/, "db.example.org");
  fill(/^Port/, "abc");
  fill(/^Database \*/, "archive");
  fill(/^User/, "archivist");
  fill(/^Connection name/, "Archive B");
  fireEvent.click(
    within(screen.getByTestId("wizard-actions")).getByRole("button", {
      name: en.connections.save,
    }),
  );

  expect(
    await screen.findByText(en.connections.invalidPort),
  ).toBeInTheDocument();
  expect(postCalls(fetchMock)).toHaveLength(0);
});

// Spec scenario "Details step action bar" (local-web-archive-wizard)
test("the details action bar shows Back, Cancel and Save and no Next", async () => {
  respondApi(okApi);
  renderAt("/archive/connection?dbms=postgresql");
  await screen.findByLabelText(/^Host/);

  const actions = screen.getByTestId("wizard-actions");
  await within(actions).findByRole("button", { name: en.connections.save });
  expect(
    within(actions)
      .getAllByRole("button")
      .map((button) => button.textContent),
  ).toEqual([
    en.archiveWizard.back,
    en.archiveWizard.cancel,
    en.connections.save,
  ]);
  expect(
    screen.queryByRole("button", { name: /next/i }),
  ).not.toBeInTheDocument();
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
    await within(screen.getByTestId("wizard-actions")).findByRole("button", {
      name: en.connections.save,
    }),
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
  fireEvent.click(
    within(screen.getByTestId("wizard-actions")).getByRole("button", {
      name: en.connections.save,
    }),
  );

  expect(
    await screen.findByRole("heading", { name: en.archive.title }),
  ).toBeInTheDocument();
  expect(postCalls(fetchMock)).toHaveLength(0);
});

test("Save is disabled while pending and clears when returning to the picker", async () => {
  let finish!: (handler: Handler) => void;
  const pending = new Promise<Handler>((resolve) => {
    finish = resolve;
  });
  const fetchMock = respondApi({
    ...okApi,
    "PUT /api/connections/7": () => pending,
  });
  renderAt("/archive/connection?connection=7");
  await screen.findByLabelText(/^Host/);
  const save = within(screen.getByTestId("wizard-actions")).getByRole(
    "button",
    { name: en.connections.save },
  );
  expect(
    screen.queryByRole("button", { name: /next/i }),
  ).not.toBeInTheDocument();
  fireEvent.click(save);
  await waitFor(() => expect(save).toBeDisabled());
  fireEvent.click(save);
  expect(putCalls(fetchMock)).toHaveLength(1);
  finish({ status: 200, body: archiveA });
  await screen.findByRole("heading", { name: en.archive.title });
  await waitFor(() =>
    expect(
      screen.queryByRole("button", { name: en.connections.save }),
    ).not.toBeInTheDocument(),
  );
});

test("the form submit path still validates and saves without the visible button", async () => {
  const fetchMock = respondApi({
    ...okApi,
    "PUT /api/connections/7": { body: archiveA },
  });
  renderAt("/archive/connection?connection=7");
  const host = await screen.findByLabelText(/^Host/);
  const form = host.closest("form")!;
  expect(
    within(form).getByRole("button", { name: en.connections.showPassword }),
  ).toHaveAttribute("type", "button");
  expect(form.querySelector('button[type="submit"]')).toBeInTheDocument();
  fireEvent.submit(form);
  await screen.findByRole("heading", { name: en.archive.title });
  expect(putCalls(fetchMock)).toHaveLength(1);
  expect(postCalls(fetchMock)).toHaveLength(0);
});

test("saving an opened saved connection updates it instead of creating a copy", async () => {
  const fetchMock = respondApi({
    ...okApi,
    "PUT /api/connections/7": {
      body: { ...archiveA, name: "Archive Renamed" },
    },
  });
  renderAt("/archive/connection?connection=7");
  await screen.findByLabelText(/^Host/);

  fill(/^Connection name/, "Archive Renamed");
  clickSave();

  await screen.findByRole("heading", { name: en.archive.title });
  expect(postCalls(fetchMock)).toHaveLength(0);
  expect(putCalls(fetchMock)).toHaveLength(1);
  const body = await (putCalls(fetchMock)[0][0] as Request).clone().json();
  expect(body).toMatchObject({ name: "Archive Renamed", port: "15432" });
  expect(body).not.toHaveProperty("password");
});

test("a taken name is shown on the name field and cleared on the next submit", async () => {
  let conflict = true;
  respondApi({
    ...okApi,
    "POST /api/connections": () =>
      conflict
        ? { status: 409, body: { title: "Conflict" } }
        : { status: 201, body: archiveA },
  });
  renderAt("/archive/connection?dbms=postgresql");
  await screen.findByLabelText(/^Host/);
  fill(/^Database \*/, "archive");
  fill(/^User/, "archivist");
  fill(/^Connection name/, "Archive A");

  clickSave();

  expect(
    await screen.findByText(en.connections.duplicateName),
  ).toBeInTheDocument();
  expect(screen.getByLabelText(/^Connection name/)).toHaveAttribute(
    "aria-invalid",
    "true",
  );
  expect(screen.queryByText(en.connections.saveError)).not.toBeInTheDocument();

  conflict = false;
  fill(/^Connection name/, "Archive B");
  clickSave();
  await screen.findByRole("heading", { name: en.archive.title });
});

test("a failed save shows the generic error until the next submit", async () => {
  let fail = true;
  respondApi({
    ...okApi,
    "POST /api/connections": () =>
      fail
        ? { status: 500, body: { title: "Error" } }
        : { status: 201, body: archiveA },
  });
  renderAt("/archive/connection?dbms=postgresql");
  await screen.findByLabelText(/^Host/);
  fill(/^Database \*/, "archive");
  fill(/^User/, "archivist");
  fill(/^Connection name/, "Archive B");

  clickSave();
  expect(await screen.findByText(en.connections.saveError)).toBeInTheDocument();

  fail = false;
  clickSave();
  await screen.findByRole("heading", { name: en.archive.title });
});

test.each(["0", "65536", "123456789012345678901234567890"])(
  "port %s outside 1..65535 is rejected",
  async (port) => {
    const fetchMock = respondApi(okApi);
    renderAt("/archive/connection?dbms=postgresql");
    await screen.findByLabelText(/^Host/);
    fill(/^Port/, port);
    fill(/^Database \*/, "archive");
    fill(/^User/, "archivist");
    fill(/^Connection name/, "Archive B");

    clickSave();

    expect(
      await screen.findByText(en.connections.invalidPort),
    ).toBeInTheDocument();
    expect(postCalls(fetchMock)).toHaveLength(0);
  },
);

test("dismissing Cancel leaves real form values intact", async () => {
  respondApi(okApi);
  renderAt("/archive/connection?dbms=postgresql");
  await screen.findByLabelText(/^Host/);
  fill(/^Host/, "edited.example.org");
  fill(/^Connection name/, "Unsaved name");
  fireEvent.click(
    screen.getByRole("button", { name: en.archiveWizard.cancel }),
  );
  fireEvent.click(
    screen.getByRole("button", { name: en.archiveWizard.cancelStay }),
  );
  expect(screen.getByLabelText(/^Host/)).toHaveValue("edited.example.org");
  expect(screen.getByLabelText(/^Connection name/)).toHaveValue("Unsaved name");
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

test.each(["?connection=7", "?dbms=postgresql"])(
  "missing session on %s shows the session message instead of the form",
  async (query) => {
    const unauthorized = { status: 401, body: { title: "Unauthorized" } };
    const fetchMock = respondApi({
      "GET /api/dbms": unauthorized,
      "GET /api/connections": unauthorized,
    });

    renderAt(`/archive/connection${query}`);

    expect(
      (await screen.findAllByText(en.session.missing)).length,
    ).toBeGreaterThan(0);
    expect(screen.queryByLabelText(/^Host/)).not.toBeInTheDocument();
    expect(screen.queryByDisplayValue(archiveA.host)).not.toBeInTheDocument();
    expect(postCalls(fetchMock)).toHaveLength(0);
    expect(putCalls(fetchMock)).toHaveLength(0);
  },
);
