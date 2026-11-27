import { fireEvent, screen, waitFor, within } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import i18n from "../i18n";
import en from "../i18n/en.json";
import {
  archiveHandler,
  type Handler,
  mockApi,
  renderBrowser,
  VIEW1,
} from "../test/archive";

// TSIMPLE of sample.siard (siard-api test resources): 4 rows, these columns
const TSIMPLE = {
  name: "TSIMPLE",
  rows: 4,
  columns: [
    "CCHAR",
    "CVARCHAR",
    "CCLOB",
    "CNCHAR",
    "CNCHAR_VARYING",
    "CNCLOB",
    "CXML",
    "CBINARY",
    "CVARBINARY",
    "CBLOB",
    "CNUMERIC",
    "CDECIMAL",
    "CSMALLINT",
    "CINTEGER",
    "CBIGINT",
    "CFLOAT",
    "CREAL",
    "CDOUBLE",
    "CBOOLEAN",
    "CDATE",
    "CTIME",
    "CTIMESTAMP",
    "CINTERVALYEAR",
    "CINTERVALDAY",
    "CINTERVALSECOND",
  ],
};
const TCOMPLEX = {
  name: "TCOMPLEX",
  rows: 2,
  columns: ["ID", "CARRAY", "CUDT", "CDISTINCT", "CXML"],
};

/** The grid's data rows, without its header row. */
function dataRows() {
  return within(screen.getByRole("grid")).getAllByRole("row").slice(1);
}

describe("ArchiveBrowser", () => {
  // spec requirement "Shared browse views", scenario "Browse a table"
  it("browses a table", async () => {
    mockApi(archiveHandler([TSIMPLE, TCOMPLEX]));
    renderBrowser();

    fireEvent.click(await screen.findByRole("link", { name: "TSIMPLE" }));

    await screen.findAllByText("r3");
    expect(dataRows()).toHaveLength(4);
    for (const column of TSIMPLE.columns) {
      expect(
        screen.getByRole("columnheader", { name: column }),
      ).toBeInTheDocument();
    }
    expect(screen.getByTestId("row-count")).toHaveTextContent("4");
  });

  // spec requirement "Shared browse views", scenario "Paging requests only the needed page"
  it("requests only the needed page", async () => {
    const requests = mockApi(
      archiveHandler([
        { name: "payment", rows: 16049, columns: ["payment_id", "amount"] },
      ]),
    );
    renderBrowser("/schemas/0/tables/0");
    await screen.findAllByText("r99");

    fireEvent.click(screen.getByRole("button", { name: /next page/i }));
    await screen.findAllByText("r199");
    fireEvent.click(screen.getByRole("button", { name: /next page/i }));
    await screen.findAllByText("r299");

    const thirdPage = requests.filter(
      (url) =>
        url.pathname.endsWith("/rows") &&
        url.searchParams.get("offset") === "200" &&
        url.searchParams.get("limit") === "100",
    );
    expect(thirdPage).toHaveLength(1);
    expect(dataRows()).toHaveLength(100);
    expect(screen.getAllByText("r200")).not.toHaveLength(0);
    expect(screen.queryAllByText("r199")).toHaveLength(0);
  });

  // spec requirement "Shared browse views", scenario "Search jumps to the match"
  it("search jumps to the match", async () => {
    // the first scan stops at its budget; continued from nextRow, the second one finds row 750
    const searches: URLSearchParams[] = [];
    const requests = mockApi(
      archiveHandler(
        [{ name: "payment", rows: 16049, columns: ["payment_id", "amount"] }],
        (query) => {
          searches.push(query);
          return query.get("afterRow") === "-1"
            ? { found: false, complete: false, nextRow: 500 }
            : { found: true, row: 750, column: 1, complete: false };
        },
      ),
    );
    renderBrowser("/schemas/0/tables/0");
    await screen.findAllByText("r99");

    fireEvent.change(screen.getByLabelText(en.search.table.dialog.search), {
      target: { value: "r750" },
    });
    fireEvent.click(screen.getByRole("button", { name: en.button.next }));

    await screen.findAllByText("r750");
    await waitFor(() =>
      expect(
        document.querySelector('[role="row"][data-id="750"]'),
      ).toHaveAttribute("aria-selected", "true"),
    );
    expect(searches.map((query) => query.get("afterRow"))).toEqual([
      "-1",
      "499",
    ]);
    expect(searches.every((query) => query.get("q") === "r750")).toBe(true);
    const page = requests
      .filter((url) => url.pathname.endsWith("/rows"))
      .at(-1);
    expect(page?.searchParams.get("offset")).toBe("700");
    expect(dataRows()).toHaveLength(100);
  });

  /** The payment table, whose `endpoint` requests answer `status` once `fail.now` is set. */
  function failingRows(status: number, endpoint = "/rows") {
    const fail = { now: false };
    const base = archiveHandler([
      { name: "payment", rows: 16049, columns: ["payment_id", "amount"] },
    ]);
    const handler: Handler = (path, query) =>
      fail.now && path.endsWith(endpoint)
        ? [status, { status }]
        : base(path, query);
    return { fail, handler };
  }

  // spec requirement "Shared browse views", scenario "Archive no longer open"
  it("shows that the archive is no longer open", async () => {
    const { fail, handler } = failingRows(404);
    mockApi(handler);
    renderBrowser("/schemas/0/tables/0");
    await screen.findAllByText("r99");

    fail.now = true;
    fireEvent.click(screen.getByRole("button", { name: /next page/i }));

    expect(
      await screen.findByText(/not or no longer open/),
    ).toBeInTheDocument();
    expect(screen.queryByRole("grid")).not.toBeInTheDocument();
    expect(screen.queryAllByText(/^r\d+$/)).toHaveLength(0);
  });

  // spec requirement "Shared browse views", scenario "Archive no longer open", for a search request
  it("shows that the archive is no longer open when a search fails", async () => {
    const { fail, handler } = failingRows(404, "/search");
    mockApi(handler);
    renderBrowser("/schemas/0/tables/0");
    await screen.findAllByText("r99");

    fail.now = true;
    fireEvent.change(screen.getByLabelText(en.search.table.dialog.search), {
      target: { value: "r750" },
    });
    fireEvent.click(screen.getByRole("button", { name: en.button.next }));

    expect(
      await screen.findByText(/not or no longer open/),
    ).toBeInTheDocument();
    expect(screen.queryAllByText(/^r\d+$/)).toHaveLength(0);
  });

  // spec requirement "Shared browse views", scenario "Session lost"
  it("reports a lost session to the host app", async () => {
    const { fail, handler } = failingRows(401);
    mockApi(handler);
    const onUnauthorized = vi.fn();
    renderBrowser("/schemas/0/tables/0", onUnauthorized);
    await screen.findAllByText("r99");

    fail.now = true;
    fireEvent.click(screen.getByRole("button", { name: /next page/i }));

    await waitFor(() => expect(onUnauthorized).toHaveBeenCalledTimes(1));
    expect(screen.queryByRole("grid")).not.toBeInTheDocument();
    expect(screen.queryAllByText(/^r\d+$/)).toHaveLength(0);
  });

  // spec requirement "Shared browse views", scenario "Language"
  it("shows its labels in German", async () => {
    await i18n.changeLanguage("de");
    mockApi(archiveHandler([TSIMPLE, TCOMPLEX]));
    renderBrowser("/schemas/0/tables/0");
    await screen.findAllByText("r3");

    expect(screen.getByText(/Datensätze/)).toBeInTheDocument();
    expect(screen.getByLabelText("Suche")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Weiter" })).toBeInTheDocument();
    expect(
      screen.getByLabelText("Gross-/Kleinschreibung beachten"),
    ).toBeInTheDocument();
    // the grid's own texts follow the language too
    expect(screen.getByText(/1–4 von 4/)).toBeInTheDocument();
    // no raw translation key such as "tableContainer.labelNumberOfRows"
    expect(document.body.textContent).not.toMatch(
      /\b(archive|tableContainer|search|button)\.[A-Za-z.]+/,
    );
  });

  // spec requirement "Shared browse views", scenario "LOB cell offers a download"
  it("offers a download for a LOB cell", async () => {
    const base = archiveHandler([
      { name: "TSIMPLE", rows: 1, columns: ["CINTEGER", "CBLOB"] },
    ]);
    const handler: Handler = (path, query) =>
      path.endsWith("/rows")
        ? [
            200,
            {
              total: 1,
              offset: 0,
              rows: [
                {
                  row: 0,
                  cells: [
                    { type: "text", value: "1", truncated: false },
                    { type: "lob", length: 2048 },
                  ],
                },
              ],
            },
          ]
        : base(path, query);
    mockApi(handler);
    renderBrowser("/schemas/0/tables/0");

    const link = await screen.findByRole("link", {
      name: en.archive.lob.download,
    });
    expect(link).toHaveTextContent("2048 bytes");
    expect(link).toHaveAttribute(
      "href",
      "/api/archives/a1/schemas/0/tables/0/rows/0/columns/1/content",
    );
    expect(link).toHaveAttribute("download");
  });

  // spec requirement "Shared browse views", scenario "View shows its query and columns"
  it("shows a view's query and columns", async () => {
    mockApi(archiveHandler([TSIMPLE]));
    renderBrowser();

    fireEvent.click(await screen.findByRole("link", { name: "VIEW1" }));

    expect(await screen.findByText(VIEW1.query)).toBeInTheDocument();
    const columns = screen.getByRole("table", {
      name: en.tableContainer.title.columns,
    });
    for (const column of VIEW1.columns) {
      const row = within(columns).getByRole("row", {
        name: new RegExp(column.name),
      });
      expect(row).toHaveTextContent(column.type);
    }
  });

  // spec requirement "Shared browse views", scenario "Switch between tables and views"
  it("switches between tables and views from any page", async () => {
    mockApi(archiveHandler([TSIMPLE, TCOMPLEX]));
    renderBrowser();
    const tree = {
      TSIMPLE: "/archives/a1/schemas/0/tables/0",
      TCOMPLEX: "/archives/a1/schemas/0/tables/1",
      VIEW1: "/archives/a1/schemas/0/views/0",
    };

    for (const name of ["TSIMPLE", "TCOMPLEX", "VIEW1", "TSIMPLE"] as const) {
      fireEvent.click(await screen.findByRole("link", { name }));
      expect(
        await screen.findByRole("heading", {
          level: 2,
          name: new RegExp(name),
        }),
      ).toBeInTheDocument();
      // the tree's links stay the same wherever the browser is
      for (const [link, href] of Object.entries(tree)) {
        expect(screen.getByRole("link", { name: link })).toHaveAttribute(
          "href",
          href,
        );
      }
    }
  });
});
