import { defineConfig, devices } from "@playwright/test";

// The smoke test starts the packaged agent itself (e2e/agent.ts); run it through
// ./gradlew :siard-suite-local-web-app:e2eTest, which builds the agent and passes its location.
export default defineConfig({
  testDir: "e2e",
  // one agent process per test; the smoke test is a single sequential flow
  workers: 1,
  forbidOnly: !!process.env.CI,
  reporter: process.env.CI ? [["list"], ["html", { open: "never" }]] : "list",
  use: {
    trace: "retain-on-failure",
  },
  projects: [
    {
      name: "chromium",
      // Playwright's own pinned Chromium build, installed by the playwrightInstall Gradle task
      use: { ...devices["Desktop Chrome"] },
    },
  ],
});
