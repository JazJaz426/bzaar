import { expect, test } from "@playwright/test";
import { clearUser } from "../../src/utils/api";

/**
  The general shapes of tests in Playwright Test are:
    1. Navigate to a URL
    2. Interact with the page
    3. Assert something about the page against your expectations
  Look for this pattern in the tests below!
 */

const SPOOF_UID = "mock-user-id";

test.beforeEach(
  "add spoof uid cookie to browser",
  async ({ context, page }) => {
    // - Add "uid" cookie to the browser context
    await context.addCookies([
      {
        name: "uid",
        value: SPOOF_UID,
        url: "http://localhost:8000",
      },
    ]);

    // wipe everything for this spoofed UID in the database.
    await clearUser(SPOOF_UID);
  }
);

/**
 * Don't worry about the "async" yet. We'll cover it in more detail
 * for the next sprint. For now, just think about "await" as something
 * you put before parts of your test that might take time to run,
 * like any interaction with the page.
 */
test("on page load, I see the map screen without authing.", async ({
  page,
}) => {
  // Notice: http, not https! Our front-end is not set up for HTTPs.
  await page.goto("http://localhost:8000/");
  await expect(page.getByLabel("Redlining Map")).toBeVisible();
});

test("I can see the map component", async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await expect(page.getByText("Redlining Map")).toBeVisible();

  await expect(page.getByLabel("Map display")).toBeVisible();
});

test("I can add and remove a pin", async ({ page }) => {
  await clearUser(SPOOF_UID);
  await page.goto("http://localhost:8000/");
  const map = page.getByLabel("Map display");
  await map.click({ position: { x: 200, y: 200 } });
  const pin = await page.getByLabel("Map marker");
  await expect(pin).toBeVisible();
  await pin.click();
  await expect(pin).not.toBeVisible();
});

test("Pins persist through page reloads", async ({ page }) => {
  await clearUser(SPOOF_UID);
  await page.goto("http://localhost:8000/");
  const map = page.getByLabel('Map display');
  await map.click({ position: { x: 200, y: 200 } });
  await expect(page.getByLabel("Map marker").first()).toBeVisible();
  await page.reload();
  await page.waitForTimeout(500);
  await expect(page.getByLabel("Map marker").first()).toBeVisible();

});

test("I can add multiple pins and remove a specific pins", async ({ page }) => {
  await clearUser(SPOOF_UID);
  await page.goto("http://localhost:8000/");
  const map = page.getByLabel("Map display");


  await map.click({ position: { x: 100, y: 100 } });
  await page.waitForTimeout(500);
  await expect(page.getByLabel("Map marker").first()).toBeVisible();

  // Add the second pin
  await map.click({ position: { x: 300, y: 300 } });
  await page.waitForTimeout(500); // Add a delay to ensure pin2 is rendered
  await expect(page.getByLabel("Map marker").nth(1)).toBeVisible();

  // Add the third pin
  await map.click({ position: { x: 400, y: 400 } });
  await page.waitForTimeout(500); // Add a delay to ensure pin3 is rendered
  await expect(page.getByLabel("Map marker").nth(2)).toBeVisible();
  
  // Remove the second pin
  await page.getByLabel("Map marker").nth(1).click();

  // Check there are only 2 pins
  await expect(page.getByLabel("Map marker").first()).toBeVisible();
  await expect(page.getByLabel("Map marker").nth(1)).toBeVisible();
  await expect(page.getByLabel("Map marker").nth(2)).not.toBeVisible();
});



test("I can filter the overlay and the page does not crash", async ({ page }) => {
  await page.goto("http://localhost:8000/");
  const inputField = page.getByLabel("overlay-filter-field");
  await inputField.fill("test");
  await page.getByLabel("filter-button").click();
  // Not sure how to test the overlay is correct, just test the page doesn't crash
});


// await page.reload();