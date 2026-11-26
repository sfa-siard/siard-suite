import { fireEvent, screen } from "@testing-library/react";
import { expect, test } from "vitest";
import en from "@siard-web/shared/i18n/en.json";
import { renderAt } from "../test/render";

const mappings = [
  { name: en.startPage.archive, red: ["source-database", "siard"] },
  { name: en.startPage.open, red: ["siard"] },
  { name: en.startPage.upload, red: ["siard", "destination-database"] },
  { name: en.startPage.export, red: ["siard", "destination-table"] },
];
const artwork = [
  "source-database",
  "siard",
  "destination-database",
  "destination-table",
];
function expectArtwork(red: string[]) {
  for (const id of artwork) {
    expect(screen.getByTestId(id).getAttribute("src")?.includes("_red")).toBe(
      red.includes(id),
    );
  }
}

test.each(mappings)(
  "$name highlights only its artwork and resets on exit",
  ({ name, red }) => {
    renderAt("/");
    const button = screen.getByRole("button", { name });
    expectArtwork([]);
    fireEvent.pointerEnter(button);
    expectArtwork(red);
    fireEvent.pointerLeave(button);
    expectArtwork([]);
    fireEvent.focus(button);
    expectArtwork(red);
    fireEvent.blur(button);
    expectArtwork([]);
  },
);

test("pointer feedback overrides focus and restores it on exit", () => {
  renderAt("/");
  const open = screen.getByRole("button", { name: en.startPage.open });
  const archive = screen.getByRole("button", { name: en.startPage.archive });
  fireEvent.focus(open);
  fireEvent.pointerEnter(archive);
  expectArtwork(["source-database", "siard"]);
  fireEvent.pointerLeave(archive);
  expectArtwork(["siard"]);
  fireEvent.blur(open);
  expectArtwork([]);
});
