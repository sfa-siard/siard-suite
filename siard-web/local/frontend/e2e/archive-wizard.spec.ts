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

// Spec scenario "Desktop field arrangement" of local-web-connections: at 1280 by 800 CSS
// pixels server and a compact port share the upper-left half, database name is upper right,
// username and password follow below them, and options, the JDBC URL and the connection name
// span the form.

test("connection details field arrangement at 1280px", async ({ browser }) => {
  const agent = await startAgent(10);
  const context = await browser.newContext({
    locale: "en",
    viewport: { width: 1280, height: 800 },
  });
  try {
    const page = await context.newPage();
    await page.goto(agent.launchUrl);
    await expect(page.getByRole("region", { name: "SIARD" })).toBeVisible();
    await page.goto(
      `http://127.0.0.1:${agent.port}/archive/connection?dbms=postgresql`,
    );
    await expect(page.getByLabel(/^Host/)).toBeVisible();

    const box = async (label: RegExp) => {
      const found = await page.getByLabel(label).boundingBox();
      expect(found, `bounding box of ${label}`).not.toBeNull();
      return found!;
    };
    const host = await box(/^Host/);
    const port = await box(/^Port/);
    const dbName = await box(/^Database/);
    const user = await box(/^User/);
    const password = await box(/^Password/);
    const options = await box(/^Options/);
    const jdbcUrl = await box(/^JDBC URL/);
    const name = await box(/^Connection name/);
    const near = (a: number, b: number) => Math.abs(a - b) <= 1;

    // first row: server, compact port, database name
    expect(near(host.y, port.y) && near(host.y, dbName.y)).toBe(true);
    expect(port.x).toBeGreaterThan(host.x + host.width);
    expect(port.width).toBeLessThan(host.width);
    expect(dbName.x).toBeGreaterThan(port.x + port.width);
    // second row: username below server, password below database name
    expect(user.y).toBeGreaterThan(host.y + host.height);
    expect(near(user.x, host.x)).toBe(true);
    expect(near(password.y, user.y)).toBe(true);
    expect(near(password.x, dbName.x)).toBe(true);
    // additional fields, the JDBC URL, then the connection name last, all spanning the form
    expect(options.y).toBeGreaterThan(user.y + user.height);
    expect(jdbcUrl.y).toBeGreaterThan(options.y + options.height);
    expect(name.y).toBeGreaterThan(jdbcUrl.y + jdbcUrl.height);
    for (const wide of [options, jdbcUrl, name]) {
      expect(near(wide.x, host.x)).toBe(true);
      expect(near(wide.x + wide.width, dbName.x + dbName.width)).toBe(true);
    }
    await expect(page.getByLabel(/database system/i)).toHaveCount(0);
    await expect(page.getByRole("checkbox")).toHaveCount(0);
  } finally {
    await context.close();
    agent.stop();
  }
});

// Spec scenario "Narrow form stacks without overflow" of local-web-connections: at 360 by 800
// CSS pixels the inputs stack in form order, including the JDBC URL, without horizontal scrolling.

test(`connection details inputs stack at ${WIDTH}px`, async ({ browser }) => {
  const agent = await startAgent(10);
  const context = await browser.newContext({
    locale: "en",
    viewport: { width: WIDTH, height: 800 },
  });
  try {
    const page = await context.newPage();
    await page.goto(agent.launchUrl);
    await expect(page.getByRole("region", { name: "SIARD" })).toBeVisible();
    await page.goto(
      `http://127.0.0.1:${agent.port}/archive/connection?dbms=postgresql`,
    );
    await expect(page.getByLabel(/^Host/)).toBeVisible();

    let previousBottom = -Infinity;
    for (const label of [
      /^Host/,
      /^Port/,
      /^Database/,
      /^User/,
      /^Password/,
      /^Options/,
      /^JDBC URL/,
      /^Connection name/,
    ]) {
      const input = page.getByLabel(label);
      await input.scrollIntoViewIfNeeded();
      await expect(input).toBeInViewport();
      const found = await input.evaluate((element) => {
        const rect = element.getBoundingClientRect();
        const view = element.ownerDocument.defaultView!;
        return { top: rect.top + view.scrollY, height: rect.height };
      });
      expect(found.top, `${label} below the previous input`).toBeGreaterThan(
        previousBottom,
      );
      previousBottom = found.top + found.height;
    }
    expect(
      await page.locator("html").evaluate((element) => element.scrollWidth),
    ).toBeLessThanOrEqual(WIDTH);
  } finally {
    await context.close();
    agent.stop();
  }
});

// Spec scenario "Wizard steps share a content width" of local-web-connections: picker and
// details use the same centered container of at most 960 CSS pixels, their headings share a
// left edge, and the container shrinks to the available width on narrow screens.

for (const width of [1280, 360]) {
  test(`wizard steps share a content width at ${width}px`, async ({
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
      const origin = `http://127.0.0.1:${agent.port}`;

      type Container = {
        width: number;
        leftGap: number;
        rightGap: number;
        available: number;
      };
      const measured: { headingX: number; container: Container }[] = [];
      for (const [path, title] of [
        ["/archive", en.archive.title],
        ["/archive/connection?dbms=postgresql", en.connections.newTitle],
      ]) {
        await page.goto(`${origin}${path}`);
        const heading = page.getByRole("heading", {
          level: 1,
          name: new RegExp(`^${title}`),
        });
        await expect(heading).toBeVisible();
        await expect(page.getByRole("progressbar")).toHaveCount(0);
        measured.push(
          await heading.evaluate((element) => {
            const view = element.ownerDocument.defaultView!;
            const styleOf = (target: typeof element) =>
              view.getComputedStyle(target);
            let container: typeof element | null = element;
            while (container && styleOf(container).maxWidth !== "960px") {
              container = container.parentElement;
            }
            if (!container) {
              throw new Error("no 960px wizard container around the heading");
            }
            const parent = container.parentElement!.getBoundingClientRect();
            const box = container.getBoundingClientRect();
            const style = styleOf(container.parentElement!);
            return {
              headingX: element.getBoundingClientRect().x,
              container: {
                width: box.width,
                // free space left and right inside the parent's content box
                leftGap: box.left - parent.left - parseFloat(style.paddingLeft),
                rightGap:
                  parent.right - parseFloat(style.paddingRight) - box.right,
                available:
                  parent.width -
                  parseFloat(style.paddingLeft) -
                  parseFloat(style.paddingRight),
              },
            };
          }),
        );
      }

      const [picker, details] = measured;
      expect(Math.abs(picker.headingX - details.headingX)).toBeLessThanOrEqual(
        1,
      );
      for (const { container: box } of measured) {
        expect(box.width).toBeLessThanOrEqual(960);
        if (box.available >= 960) {
          expect(Math.abs(box.width - 960)).toBeLessThanOrEqual(1);
          expect(Math.abs(box.leftGap - box.rightGap)).toBeLessThanOrEqual(1);
        } else {
          expect(Math.abs(box.width - box.available)).toBeLessThanOrEqual(1);
        }
      }
      if (width < 960) {
        // DBMS choices wrap inside the narrow container
        await page.goto(`${origin}/archive`);
        for (const name of ["PostgreSQL", "DB/2"]) {
          const choice = page.getByRole("button", { name });
          await choice.scrollIntoViewIfNeeded();
          const box = (await choice.boundingBox())!;
          expect(box.x).toBeGreaterThanOrEqual(0);
          expect(box.x + box.width).toBeLessThanOrEqual(width);
        }
      }
    } finally {
      await context.close();
      agent.stop();
    }
  });
}
