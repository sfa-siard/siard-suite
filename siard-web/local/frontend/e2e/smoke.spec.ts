import { expect, test } from "@playwright/test";
import { required, startAgent, type Agent } from "./agent";

// The smoke test of design D12: launch URL → clean URL and session cookie → About shows the
// version → page closed → agent exits. It covers the chain no other test covers end to end:
// a real browser following the token redirect, keeping the event stream open and closing it.

const GRACE_SECONDS = 2;

let agent: Agent | undefined;

test.afterEach(() => agent?.stop());

test("launch, About view and exit after the window closes", async ({
  browser,
}) => {
  agent = await startAgent(GRACE_SECONDS);
  const origin = `http://127.0.0.1:${agent.port}`;
  const context = await browser.newContext();
  const page = await context.newPage();

  await page.goto(agent.launchUrl);

  // the token is gone from the address bar and a session cookie is set
  await expect(page).toHaveURL(`${origin}/`);
  const cookies = await context.cookies(origin);
  expect(cookies).toContainEqual(
    expect.objectContaining({
      name: `siard_session_${agent.port}`,
      httpOnly: true,
      sameSite: "Strict",
    }),
  );

  // client-side navigation, as a user would do it
  await page.getByRole("link", { name: "Info" }).click();
  await expect(page).toHaveURL(`${origin}/about`);
  await expect(
    page.getByRole("heading", {
      name: `SIARD Suite ${required("E2E_APP_VERSION")}`,
    }),
  ).toBeVisible();

  // closing the only window closes its event stream; the agent exits after the grace period
  await context.close();
  const exitCode = await Promise.race([
    agent.exited,
    new Promise<"timeout">((resolve) =>
      setTimeout(() => resolve("timeout"), (GRACE_SECONDS + 5) * 1000),
    ),
  ]);
  expect(
    exitCode,
    "agent still running after the grace period plus 5 s",
  ).not.toBe("timeout");
});
