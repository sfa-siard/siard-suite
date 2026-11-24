import { expect, test } from "vitest";
import { archiveSteps, resolveArchiveStep } from "./archiveSteps";

test("declares implemented steps in workflow order", () => {
  expect(archiveSteps).toEqual([
    { path: "/archive", titleKey: "archiveWizard.selectConnection" },
    {
      path: "/archive/connection",
      titleKey: "archiveWizard.connectionDetails",
    },
  ]);
});

test.each([
  ["/archive", 0],
  ["/archive/", 0],
  ["/archive/connection", 1],
  ["/archive/connection/", 1],
  ["/archive/connection/details", 1],
  ["/", -1],
  ["/about", -1],
  ["/archives", -1],
])("resolves %s to step %i", (pathname, expected) => {
  expect(resolveArchiveStep(pathname)).toBe(expected);
});
