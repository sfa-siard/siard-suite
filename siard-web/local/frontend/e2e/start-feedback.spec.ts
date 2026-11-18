import { expect, test } from "@playwright/test";
import { startAgent } from "./agent";

for (const width of [1280, 360]) {
  test(`feedback, motion and inert controls at ${width}px`, async ({
    browser,
  }) => {
    const agent = await startAgent(10);
    const context = await browser.newContext({
      locale: "en",
      viewport: { width, height: 800 },
    });
    try {
      const page = await context.newPage();
      await page.goto(agent.launchUrl);
      await expect(page.getByRole("region", { name: "SIARD" })).toBeVisible();
      await page
        .locator("html")
        .evaluate((element) =>
          element.ownerDocument.fonts.ready.then(() => undefined),
        );
      const unexpected: string[] = [];
      page.on("dialog", (dialog) => {
        unexpected.push("dialog");
        void dialog.dismiss();
      });
      page.on("filechooser", () => unexpected.push("filechooser"));
      page.on("download", () => unexpected.push("download"));
      page.on("popup", () => unexpected.push("popup"));
      page.on("request", (request) => {
        const url = new URL(request.url());
        if (
          request.resourceType() !== "image" &&
          request.resourceType() !== "font" &&
          request.resourceType() !== "stylesheet" &&
          url.pathname !== "/api/events"
        ) {
          unexpected.push(`${request.method()} ${url.pathname}`);
        }
        expect(url.searchParams.has("token")).toBe(false);
        expect(request.postData()).toBeNull();
      });
      const ids = [
        "source-database",
        "siard",
        "destination-database",
        "destination-table",
      ];
      const original = await Promise.all(
        ids.map((id) =>
          page.getByTestId(id).evaluate((image) => ({
            width: image.getBoundingClientRect().width,
            height: image.getBoundingClientRect().height,
          })),
        ),
      );
      for (const [name, red] of [
        ["archive", ["source-database", "siard"]],
        ["open", ["siard"]],
        ["upload", ["siard", "destination-database"]],
        ["export", ["siard", "destination-table"]],
      ] as const) {
        const button = page.locator(`[data-action="${name}"]`);
        await button.hover();
        for (const id of ids) {
          const src = await page.getByTestId(id).getAttribute("src");
          expect(src?.includes("_red")).toBe(
            (red as readonly string[]).includes(id),
          );
        }
        if (name !== "open") {
          const dot = page.getByTestId(`${name}-dot`);
          await expect(dot).toBeVisible();
          const positions = await dot.evaluate(async (element) => {
            const animation = element.getAnimations()[0];
            animation.pause();
            animation.currentTime = 100;
            const first = element.getBoundingClientRect().toJSON();
            animation.currentTime = 1200;
            const last = element.getBoundingClientRect().toJSON();
            return { first, last };
          });
          expect(
            width === 1280 ? positions.last.x : positions.last.y,
          ).toBeGreaterThan(
            width === 1280 ? positions.first.x : positions.first.y,
          );
        } else {
          await expect(page.locator(".workflow-dot")).toHaveCount(0);
        }
        for (const activate of [
          () => button.click(),
          () => button.press("Enter"),
          () => button.press("Space"),
        ]) {
          await activate();
          await expect(page).toHaveURL(`http://127.0.0.1:${agent.port}/`);
          await expect(page.getByRole("dialog")).toHaveCount(0);
        }
        await page.mouse.move(0, 0);
        await button.evaluate((element) => element.blur());
        await expect(page.locator(".workflow-dot")).toHaveCount(0);
        for (const id of ids) {
          expect(
            (await page.getByTestId(id).getAttribute("src"))?.includes("_red"),
          ).toBe(false);
        }
      }
      await page.emulateMedia({ reducedMotion: "reduce" });
      await page.locator("[data-action=archive]").focus();
      for (const name of ["archive", "open", "upload", "export"]) {
        const button = page.locator(`[data-action=${name}]`);
        await expect(button).toBeFocused();
        expect(
          await button.evaluate(
            (element) =>
              element.ownerDocument.defaultView!.getComputedStyle(element)
                .outlineStyle,
          ),
        ).toBe("solid");
        await expect(page.getByTestId("siard")).toHaveAttribute("src", /_red/);
        await expect(page.locator(".workflow-dot:visible")).toHaveCount(0);
        await page.keyboard.press("Tab");
      }
      expect(
        await Promise.all(
          ids.map((id) =>
            page.getByTestId(id).evaluate((image) => ({
              width: image.getBoundingClientRect().width,
              height: image.getBoundingClientRect().height,
            })),
          ),
        ),
      ).toEqual(original);
      const body = await page.locator("body").innerText();
      expect(body).not.toContain(
        new URL(agent.launchUrl).searchParams.get("token")!,
      );
      expect(body).not.toMatch(/password|jdbc:|\/home\/|\/Users\/|[A-Z]:\\/i);
      expect(unexpected).toEqual([]);
    } finally {
      await context.close();
      agent.stop();
    }
  });
}
