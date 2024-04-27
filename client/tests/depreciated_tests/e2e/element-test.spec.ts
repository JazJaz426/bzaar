import { expect, test } from "@playwright/test";



test.beforeEach(async({page}) => {
    await page.goto('http://localhost:8000/');

  })
  test('signout button shows up', async ({ page }) => {
    const signOutButton = page.locator('.SignOut');
    await expect(signOutButton).toBeVisible();
  });
  
test('after I type into the input box, its text changes', async ({ page }) => {
  await page.getByLabel('Command input').click();
  await page.getByLabel('Command input').fill('Awesome command');
  const mock_input = `Awesome command`
  await expect(page.getByLabel('Command input')).toHaveValue(mock_input)
});

test('on page load, i see a submit button', async ({ page }) => {
  await expect(page.getByLabel('Submit')).toBeVisible()
});
