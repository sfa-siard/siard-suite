import { expect, test } from "vitest";

// Spec scenario "Shared package ships no session handling" (design R8): the local agent's session
// cookie and launch path must not creep into the shared package. The strings are assembled so this
// file does not match itself.
const forbidden = ["siard_session" + "_", "/la" + "unch"];

const sources = import.meta.glob<string>("./**/*.{ts,tsx,css,json}", {
  query: "?raw",
  import: "default",
  eager: true,
});

test("shared sources contain no session cookie name or launch path", () => {
  expect(Object.keys(sources).length).toBeGreaterThan(5);
  const hits = Object.entries(sources).flatMap(([file, content]) =>
    forbidden
      .filter((text) => content.includes(text))
      .map((text) => `${file}: ${text}`),
  );
  expect(hits).toEqual([]);
});
