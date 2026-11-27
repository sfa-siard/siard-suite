import { describe, expect, test } from "vitest";
import { isAbsolute, normalisePastedPath } from "./normalisePastedPath";

// spec "Path field", scenario "Paste from a file manager"
describe("paths as file managers copy them", () => {
  test.each([
    [
      "Windows Explorer, Copy as path",
      '"C:\\Users\\me\\My Data\\db.siard"',
      "C:\\Users\\me\\My Data\\db.siard",
    ],
    [
      "Windows folder, quoted",
      '"C:\\Users\\me\\My Data"',
      "C:\\Users\\me\\My Data",
    ],
    [
      "macOS Finder",
      "/Users/me/My Data/db.siard",
      "/Users/me/My Data/db.siard",
    ],
    [
      "Linux URI with %20",
      "file:///home/me/My%20Data/db.siard",
      "/home/me/My Data/db.siard",
    ],
    [
      "Linux URI, non-ASCII",
      "file:///home/me/Donn%C3%A9es/z%C3%B6e.siard",
      "/home/me/Données/zöe.siard",
    ],
    [
      "URI with localhost",
      "file://localhost/home/me/db.siard",
      "/home/me/db.siard",
    ],
    ["URI with a single slash", "file:/home/me/db.siard", "/home/me/db.siard"],
    [
      "Windows drive URI",
      "file:///C:/Users/me/My%20Data/db.siard",
      "C:\\Users\\me\\My Data\\db.siard",
    ],
    ["Windows drive root URI", "file:///D:/", "D:\\"],
    [
      "UNC URI",
      "file://server/share/My%20Data/db.siard",
      "\\\\server\\share\\My Data\\db.siard",
    ],
    ["UNC path", "\\\\server\\share\\db.siard", "\\\\server\\share\\db.siard"],
    [
      "surrounding whitespace and newline",
      "  /home/me/db.siard\n",
      "/home/me/db.siard",
    ],
    [
      "Windows path with forward slashes",
      "C:/Users/me/db.siard",
      "C:/Users/me/db.siard",
    ],
  ])("%s", (_name, input, expected) => {
    expect(normalisePastedPath(input)).toBe(expected);
  });
});

// spec "Path field", scenario "Malformed input is not passed on"
describe("input that is not an absolute path", () => {
  test.each([
    ["empty", ""],
    ["only whitespace", "   "],
    ["only quotes", '""'],
    ["relative", "../secret"],
    ["relative name", "db.siard"],
    ["drive without slash", "C:db.siard"],
    ["relative URI", "file:db.siard"],
    ["malformed percent-encoding", "file:///home/me/100%.siard"],
  ])("%s", (_name, input) => {
    expect(normalisePastedPath(input)).toBeNull();
  });
});

test("only one pair of surrounding quotes is removed", () => {
  expect(normalisePastedPath('"/home/me/"quoted".siard"')).toBe(
    '/home/me/"quoted".siard',
  );
  expect(normalisePastedPath('"/home/me/db.siard')).toBeNull();
});

test("absolute paths on every platform", () => {
  expect(isAbsolute("/")).toBe(true);
  expect(isAbsolute("C:\\")).toBe(true);
  expect(isAbsolute("c:/")).toBe(true);
  expect(isAbsolute("\\\\server\\share")).toBe(true);
  expect(isAbsolute("C:")).toBe(false);
  expect(isAbsolute("home/me")).toBe(false);
});
