import { expect, test } from "@playwright/test";
import en from "../../../frontend-shared/src/i18n/en.json" with { type: "json" };
import de from "../../../frontend-shared/src/i18n/de.json" with { type: "json" };
import fr from "../../../frontend-shared/src/i18n/fr.json" with { type: "json" };
import it from "../../../frontend-shared/src/i18n/it.json" with { type: "json" };
import { startAgent } from "./agent";

for (const [locale, resource] of Object.entries({ en, de, fr, it })) {
  for (const width of [1280, 760, 360]) {
    test(`${locale} overview at ${width}px`, async ({ browser }, testInfo) => {
      const agent = await startAgent(10);
      const context = await browser.newContext({
        locale,
        viewport: { width, height: 800 },
      });
      try {
        const page = await context.newPage();
        await page.goto(agent.launchUrl);
        const overview = page.getByRole("region", { name: "SIARD" });
        await expect(overview).toBeVisible();
        await page
          .locator("html")
          .evaluate((element) =>
            element.ownerDocument.fonts.ready.then(() => undefined),
          );
        for (const action of ["archive", "open", "upload", "export"] as const) {
          await expect(
            overview.getByRole("button", {
              name: resource.startPage[action],
              exact: true,
            }),
          ).toHaveCount(1);
        }
        for (const description of [
          resource.startPage.archiveDescription,
          resource.startPage.openDescription,
          resource.startPage.destinationDescription,
        ]) {
          await expect(overview).toContainText(
            description.replace(/<\/?strong>/g, ""),
          );
        }
        const source = (await page
          .getByTestId("source-database")
          .boundingBox())!;
        const siard = (await page.getByTestId("siard").boundingBox())!;
        const upload = (await page.getByTestId("upload-flow").boundingBox())!;
        const exported = (await page.getByTestId("export-flow").boundingBox())!;
        if (width === 1280) {
          expect(source.x + source.width).toBeLessThan(siard.x);
          expect(siard.x + siard.width).toBeLessThan(upload.x);
          expect(upload.y + upload.height).toBeLessThanOrEqual(exported.y);
        } else {
          expect(source.y + source.height).toBeLessThan(siard.y);
          expect(siard.y + siard.height).toBeLessThan(upload.y);
          expect(upload.y + upload.height).toBeLessThanOrEqual(exported.y);
        }
        expect(
          await page.locator("html").evaluate((element) => element.scrollWidth),
        ).toBeLessThanOrEqual(width);
        await page.screenshot({
          path: testInfo.outputPath(`${locale}-${width}.png`),
          fullPage: true,
        });
        await page.addStyleTag({
          content:
            ".start-overview p, .start-overview .workflow-button { font-size: 200%; }",
        });
        expect(
          await page.locator("html").evaluate((element) => element.scrollWidth),
        ).toBeLessThanOrEqual(width);
        for (const button of await overview.getByRole("button").all()) {
          await button.scrollIntoViewIfNeeded();
          await expect(button).toBeVisible();
        }
      } finally {
        await context.close();
        agent.stop();
      }
    });
  }
}
