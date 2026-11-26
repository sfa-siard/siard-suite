import { vi } from "vitest";
import { jsonResponse } from "@siard-web/shared/test";

/** Answers `"METHOD /path"` with [status, body]; unknown routes answer 404. */
export type Routes = Record<string, [number, unknown?]>;

export interface RecordedRequest {
  method: string;
  url: URL;
  body: string;
}

/** Stubs fetch with fixed answers per method and path; returns every request it received. */
export function stubRoutes(routes: Routes) {
  const requests: RecordedRequest[] = [];
  vi.stubGlobal(
    "fetch",
    vi.fn(async (request: Request) => {
      const url = new URL(request.url);
      requests.push({
        method: request.method,
        url,
        body: request.body ? await request.clone().text() : "",
      });
      const [status, body] = routes[`${request.method} ${url.pathname}`] ?? [
        404,
        { status: 404 },
      ];
      return status === 204
        ? new Response(null, { status })
        : jsonResponse(status, body);
    }),
  );
  return requests;
}

/** A home folder with one archive and one other file, as the file chooser sees it. */
export const HOME = "/home/me/My Data";
export const ARCHIVE_PATH = `${HOME}/db.siard`;

export const chooserRoutes: Routes = {
  "GET /api/files/places": [200, [{ kind: "home", path: HOME }]],
  "GET /api/files": [
    200,
    {
      path: HOME,
      parent: "/home/me",
      truncated: false,
      entries: [
        {
          name: "db.siard",
          path: ARCHIVE_PATH,
          folder: false,
          size: 10,
          modified: "2026-11-25T10:00:00Z",
          hidden: false,
        },
      ],
    },
  ],
};
