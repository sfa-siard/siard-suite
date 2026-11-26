import { fireEvent, screen, waitFor, within } from "@testing-library/react";
import { beforeEach, expect, test } from "vitest";
import { i18n } from "@siard-web/shared";
import en from "@siard-web/shared/i18n/en.json";
import { renderAt } from "../test/render";
import {
  ARCHIVE_PATH,
  chooserRoutes,
  HOME,
  type Routes,
  stubRoutes,
} from "../test/routes";

// Spec requirement "Inspect workflow" of local-web-archive-inspection

const ARCHIVE_ID = "a1";
const archiveRoutes: Routes = {
  [`GET /api/archives/${ARCHIVE_ID}`]: [
    200,
    {
      dbName: "Sample Database",
      schemas: [{ index: 0, name: "SampleSchema" }],
    },
  ],
  [`GET /api/archives/${ARCHIVE_ID}/schemas/0`]: [
    200,
    {
      index: 0,
      name: "SampleSchema",
      tables: [{ index: 0, name: "TSIMPLE", rows: 4 }],
      views: [],
    },
  ],
  [`DELETE /api/archives/${ARCHIVE_ID}`]: [204],
};

beforeEach(async () => {
  await i18n.changeLanguage("en");
});

/** Opens the archive through the start page: Open, double-click db.siard in the chooser. */
async function openThroughStartPage() {
  renderAt("/");
  fireEvent.click(screen.getByRole("button", { name: en.startPage.open }));
  const listing = await screen.findByRole("list", { name: HOME });
  fireEvent.doubleClick(within(listing).getByText("db.siard"));
}

test("inspect an archive and close it", async () => {
  const requests = stubRoutes({
    ...chooserRoutes,
    "POST /api/archive-files": [201, { archiveId: ARCHIVE_ID }],
    ...archiveRoutes,
  });

  await openThroughStartPage();

  expect(
    await screen.findByRole("heading", { name: "Sample Database" }),
  ).toBeInTheDocument();
  // the route is /archives/a1: the browser's links are relative to it
  expect(await screen.findByRole("link", { name: "TSIMPLE" })).toHaveAttribute(
    "href",
    `/archives/${ARCHIVE_ID}/schemas/0/tables/0`,
  );
  expect(screen.queryByRole("dialog")).not.toBeInTheDocument();

  fireEvent.click(screen.getByRole("button", { name: en.inspect.close }));

  expect(
    await screen.findByRole("region", { name: "SIARD" }),
  ).toBeInTheDocument();
  expect(
    requests
      .filter((request) => request.method === "DELETE")
      .map((request) => request.url.pathname),
  ).toEqual([`/api/archives/${ARCHIVE_ID}`]);
});

test.each([
  [422, en.inspect.cannotOpen],
  [429, en.inspect.tooMany],
  [500, en.inspect.openFailed],
])(
  "a %i answer to opening shows an error on the start page",
  async (status, text) => {
    stubRoutes({
      ...chooserRoutes,
      "POST /api/archive-files": [status, { status }],
    });

    await openThroughStartPage();

    expect(await screen.findByText(text)).toBeInTheDocument();
    expect(screen.getByRole("region", { name: "SIARD" })).toBeInTheDocument();
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
  },
);

test("an archive that is no longer open shows a message with a link back", async () => {
  stubRoutes({});

  renderAt(`/archives/${ARCHIVE_ID}`);

  expect(await screen.findByText(en.archive.notOpen)).toBeInTheDocument();
  expect(screen.queryByRole("button", { name: en.inspect.close })).toBeNull();
  fireEvent.click(screen.getByRole("link", { name: en.inspect.backToStart }));
  expect(
    await screen.findByRole("region", { name: "SIARD" }),
  ).toBeInTheDocument();
});

test("the archive's path appears only in the body of the open request", async () => {
  const requests = stubRoutes({
    ...chooserRoutes,
    "POST /api/archive-files": [201, { archiveId: ARCHIVE_ID }],
    ...archiveRoutes,
  });

  await openThroughStartPage();
  fireEvent.click(await screen.findByRole("link", { name: "TSIMPLE" }));
  await waitFor(() =>
    expect(
      requests.some((request) => request.url.pathname.endsWith("/tables/0")),
    ).toBe(true),
  );

  const opens = requests.filter(
    (request) => request.url.pathname === "/api/archive-files",
  );
  expect(opens).toHaveLength(1);
  expect(JSON.parse(opens[0].body)).toEqual({ path: ARCHIVE_PATH });
  for (const request of requests) {
    // the chooser lists the folder by its path; the archive's own path stays out of URLs
    expect(decodeURIComponent(request.url.href)).not.toContain("db.siard");
  }
  for (const link of screen.getAllByRole("link")) {
    expect(decodeURIComponent(link.getAttribute("href") ?? "")).not.toContain(
      "db.siard",
    );
  }
});
