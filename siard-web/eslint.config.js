// ESLint configuration for every package of the siard-web npm workspace (design R5). Paths are
// relative to this directory; each package runs `eslint .` in its own directory.
import { defineConfig, globalIgnores } from "eslint/config";
import js from "@eslint/js";
import tseslint from "typescript-eslint";
import reactHooks from "eslint-plugin-react-hooks";
import prettier from "eslint-config-prettier";
import globals from "globals";

export default defineConfig([
  globalIgnores([
    "**/dist",
    "**/build",
    "**/.gradle",
    "**/node_modules",
    "**/src/api/schema.d.ts",
    "**/test-results",
    "**/playwright-report",
  ]),
  {
    files: ["**/*.{ts,tsx}"],
    extends: [
      js.configs.recommended,
      tseslint.configs.recommended,
      reactHooks.configs.flat.recommended,
    ],
    languageOptions: { globals: globals.browser },
  },
  {
    files: ["**/e2e/**/*.ts", "**/*.config.{ts,js}"],
    languageOptions: { globals: globals.node },
  },
  {
    // the shared package must not depend on any app (design R8): no relative path into an app's
    // directory and no app package name
    files: ["frontend-shared/**/*.{ts,tsx}"],
    rules: {
      "no-restricted-imports": [
        "error",
        {
          patterns: [
            {
              group: ["**/local/**", "**/remote/**"],
              message:
                "frontend-shared must not import from a web app (design R8).",
            },
            {
              group: [
                "@siard-web/local",
                "@siard-web/local/*",
                "@siard-web/remote",
                "@siard-web/remote/*",
              ],
              message:
                "frontend-shared must not import from a web app (design R8).",
            },
          ],
        },
      ],
    },
  },
  prettier,
]);
