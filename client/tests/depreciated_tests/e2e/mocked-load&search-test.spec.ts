
import { expect, test } from '@playwright/test';

test.beforeEach(async ({ page }) => {
  await page.goto('http://localhost:8000/');
});

test.afterEach(async ({ page }) => {
  await page.reload();
});
test.describe('Mock server responses for file loading', () => {
    test('type in "load" command with wrong/right path expect to see file not loaded or loaded', async ({ page }) => {
      // Intercept and mock response for failed file load
      await page.route('http://localhost:3232/loadcsv?filepath=wrong/path', route => {
          route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({ message: 'No such file' }),
          });
      });
      await page.getByLabel('Command input').fill('load wrong/path');
      await page.getByLabel('Submit').click();
      await expect(
        page.getByText("Output: No such file")
      ).toBeVisible();
    });
  });
test.describe('Mock server responses for search before file loading', () => {
    test('type in "search" command before Load, expect to see error message', async ({ page }) => {
        await page.route('http://localhost:3232/searchcsv?val=1', route => {
          route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({ message: 'No file loaded' }),
          });
      });
        await page.getByLabel('Command input').fill('search 1');
        await page.getByLabel('Submit').click();
        await expect(
        page.getByText("Output: No file loaded")
        ).toBeVisible();
    });
});
test.describe('Mock server responses for search after file loading', () => {
    test('type in "search" command after Load, expect to see search results', async ({ page }) => {
        // Intercept and mock response for successful file load
        await page.route('http://localhost:3232/searchcsv?val=hihihi', route => {
          route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({ result: 'success', data: [['hihihi']]}),
          });
        });
        await page.getByLabel('Command input').fill('search hihihi');
        await page.getByLabel('Submit').click();
        await expect(
        page.getByText("hihihi")
        ).toBeVisible();
    });
    test('multi search command after Load, expect to see search results', async ({ page }) => {
        // Intercept and mock response for successful file load
        await page.route('http://localhost:3232/searchcsv?queries=hihi&multi=true', route => {
          route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({ result: 'success', data: [['hihihi']]}),
          });
        });
        await page.getByLabel('Command input').fill('search multi hihi');
        await page.getByLabel('Submit').click();
        await expect(
        page.getByText("hihihi")
        ).toBeVisible();
    });
    test('search not exist value, expect to see no search result response', async ({ page }) => {
        // Intercept and mock response for successful file load
        await page.route('http://localhost:3232/searchcsv?val=hihihi', route => {
          route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({ result: 'success', data: []}),
          });
        });
        await page.getByLabel('Command input').fill('search hihihi');
        await page.getByLabel('Submit').click();
        await expect(
        page.getByText("No match found")
        ).toBeVisible();
    });

});