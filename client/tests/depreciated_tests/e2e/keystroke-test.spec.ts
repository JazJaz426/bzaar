import { expect, test } from "@playwright/test";
//view
//enter
//mode
test.beforeEach(async({page}) => {
    await page.goto('http://localhost:8000/');
    // await page.getByLabel("Login").click();

})

test('test key enter', async ({ page }) => {
    await page.getByPlaceholder('Enter command here!').fill('load xxx');
    await page.getByPlaceholder('Enter command here!').press('Enter');
    await expect(page.getByText('Error happens when parsingjava.io.FileNotFoundException: xxx (No such file or directory)')).toBeVisible();
    await page.getByPlaceholder('Enter command here!').fill('view');
    await page.getByPlaceholder('Enter command here!').press('Enter');
    await expect(page.getByText('Output: No file loaded')).toBeVisible();

    await page.locator('body').press('Control+m');
    await expect(page.locator('p').filter({ hasText: 'Command: load xxx' }).getByRole('strong')).toBeVisible();
    await expect(page.locator('p').filter({ hasText: 'Command: view' }).getByRole('strong')).toBeVisible();
    await expect(page.locator('p').filter({ hasText: 'Command: keystroke: mode' }).getByRole('strong')).toBeVisible();

    await page.locator('body').press('Control+v');
    await expect(page.locator('div').filter({ hasText: 'Output: No file loaded' }).nth(3)).toBeVisible();
});