import { expect, test } from "vitest";
import de from "./de.json";
import en from "./en.json";
import fr from "./fr.json";
import it from "./it.json";

// The type check in index.ts only finds keys missing in a language; this also finds extra keys
// and texts whose {{placeholders}} differ from English.

type Messages = { [key: string]: string | Messages };

function flatten(messages: Messages, prefix = ""): Map<string, string> {
  const flat = new Map<string, string>();
  for (const [key, value] of Object.entries(messages)) {
    const path = prefix ? `${prefix}.${key}` : key;
    if (typeof value === "string") {
      flat.set(path, value);
    } else {
      flatten(value, path).forEach((text, nested) => flat.set(nested, text));
    }
  }
  return flat;
}

function placeholders(text: string): string[] {
  return [...text.matchAll(/\{\{\s*(\w+)\s*\}\}/g)]
    .map((match) => match[1])
    .sort();
}

const english = flatten(en);

test.each([
  ["de", de],
  ["fr", fr],
  ["it", it],
])("%s has the same keys and placeholders as en", (_locale, resource) => {
  const translated = flatten(resource as Messages);

  expect([...translated.keys()].sort()).toEqual([...english.keys()].sort());
  for (const [key, text] of english) {
    expect(placeholders(translated.get(key) ?? ""), key).toEqual(
      placeholders(text),
    );
  }
});
