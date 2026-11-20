import { expect, test } from "@playwright/test";
import en from "@siard-web/shared/i18n/en.json" with { type: "json" };
import { startAgent } from "./agent";

// Spec scenario "Narrow viewport" of local-web-archive-wizard: the step indicator and the
// action bar stay reachable at 360 by 800 CSS pixels without horizontal page scrolling.

const WIDTH = 360;

test(`archive wizard frame at ${WIDTH}px`, async ({ browser }) => {
  const agent = await startAgent(10);
  const context = await browser.newContext({
    locale: "en",
    viewport: { width: WIDTH, height: 800 },
  });
  try {
    const page = await context.newPage();
    await page.goto(agent.launchUrl);
    await expect(page.getByRole("region", { name: "SIARD" })).toBeVisible();
    const origin = `http://127.0.0.1:${agent.port}`;

    for (const path of ["/archive", "/archive/connection?dbms=postgresql"]) {
      await page.goto(`${origin}${path}`);
      const stepper = page.getByTestId("archive-stepper");
      await expect(stepper).toContainText(en.archiveWizard.selectConnection);
      await expect(stepper).toContainText(en.archiveWizard.connectionDetails);
      // measure the final layout: loaded lists or the form push the bar further down
      await expect(page.getByRole("progressbar")).toHaveCount(0);
      if (path.startsWith("/archive/connection")) {
        await expect(
          page
            .getByTestId("wizard-actions")
            .getByRole("button", { name: en.connections.save }),
        ).toBeVisible();
      }
      await page
        .locator("html")
        .evaluate((element) =>
          element.ownerDocument.fonts.ready.then(() => undefined),
        );

      for (const button of await page
        .getByTestId("wizard-actions")
        .getByRole("button")
        .all()) {
        await button.scrollIntoViewIfNeeded();
        await expect(button).toBeInViewport();
      }
      await stepper.scrollIntoViewIfNeeded();
      await expect(stepper).toBeInViewport();
      expect(
        await page.locator("html").evaluate((element) => element.scrollWidth),
        `scroll width on ${path}`,
      ).toBeLessThanOrEqual(WIDTH);
    }
  } finally {
    await context.close();
    agent.stop();
  }
});
