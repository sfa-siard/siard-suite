import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";

// dev: the backend runs via quarkusDev on 127.0.0.1:8080 (design D13). changeOrigin makes the
// backend see Host 127.0.0.1:8080, which its Host check requires.
const backend = { target: "http://127.0.0.1:8080", changeOrigin: true };

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/api": backend,
      "/launch": backend,
    },
  },
  test: {
    environment: "jsdom",
  },
});
