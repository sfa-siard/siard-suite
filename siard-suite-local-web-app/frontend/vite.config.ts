import { configDefaults, defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";

// dev: the backend runs via quarkusDev on 127.0.0.1:8080 (design D13). changeOrigin makes the
// backend see Host 127.0.0.1:8080, which its Host check requires.
const backend = { target: "http://127.0.0.1:8080", changeOrigin: true };

export default defineConfig({
  plugins: [react()],
  server: {
    host: "127.0.0.1",
    proxy: {
      "/api": backend,
      "/launch": backend,
    },
  },
  test: {
    environment: "jsdom",
    setupFiles: ["src/test/setup.ts"],
    // e2e/ holds the Playwright smoke test, run by the e2eTest Gradle task
    exclude: [...configDefaults.exclude, "e2e/**"],
  },
});
