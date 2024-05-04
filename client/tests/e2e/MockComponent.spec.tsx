import { test, expect } from '@playwright/test';


test.describe('ItemForm', () => {
  test('submits the form and calls postItem', async ({ page }) => {
    await page.route('**/postItem', (route, request) => {
      if (request.method().toUpperCase() === 'POST') {
        // Fulfill the request with a mocked response
        route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ message: 'Item added successfully' }),
        });
      } else {
        // Continue with the actual request if it's not a POST request
        route.continue();
      }
    });

    // Navigate to your application's form page
    const response = await page.goto('http://localhost:8000/', { waitUntil: 'domcontentloaded', timeout: 5000 });

    await page.waitForSelector('#post-button', { state: 'visible' , timeout: 10000 });
    await page.click('#post-button');
    // Fill the form fields
    await page.fill('input[placeholder="Title"]', 'Test Item');
    await page.fill('input[placeholder="Price"]', '100');
    await page.fill('input[placeholder="Condition"]', 'New');
    await page.fill('textarea[placeholder="Description"]', 'Test Description');
    page.once('dialog', async dialog => {
      console.log(dialog.message());  // Log the alert message
      expect(dialog.message()).toBe('Item added successfully');
      await dialog.dismiss();  // Or dialog.accept() if it's an OK alert
    });
    // // Click the save button
    await page.click('text=Save');
    await page.screenshot({ path: 'debug-after-save.png' });
  });
});
test.describe('Discover', () => {
  test('navigates to the Discover page and checks if it has loaded', async ({ page }) => {
    // Navigate to your application's Discover page
    await page.goto('http://localhost:8000/');

    // Click on the Discover link in the navigation
    await page.click('text=Discover');

    // Check if the Discover page has loaded by looking for a specific element
    const element = await page.waitForSelector('.item-container', { state: 'visible' , timeout: 10000 });

    // If the element is found, the Discover page has loaded successfully
    expect(element).toBeTruthy();

    // Optionally, take a screenshot for debugging
    await page.screenshot({ path: 'debug-discover-page.png' });
  });
});
test.describe('Profile', () => {
  test('navigates to the Profile page and checks if it has loaded', async ({ page }) => {
    // Mock the response to the getUserProfile API call
    await page.route('**/getUserProfile', route => {
      route.fulfill({
        status: 200, // Mimicking a successful API call
        contentType: 'application/json', // Ensuring the content type is set correctly
        body: JSON.stringify({
          status: 200,
          data: { name: 'Test User', email: 'test@example.com', address: '123 Test St'}
        }) // Mocking the API response data
      });
    });

    // Navigate to your application's Profile page
    await page.goto('http://localhost:8000/profile');
    await page.screenshot({ path: 'debug-profile-page.png' });
    // Check if the Profile page has loaded by looking for a specific element
    const element = await page.waitForSelector('.profile-section', { state: 'visible' , timeout: 10000 });
// Get the text content of the element
    const textContent = await element.textContent();

// Check if the text includes "Test User"
    expect(textContent).toContain('My Email');


    // Optionally, take a screenshot for debugging
    await page.screenshot({ path: 'debug-profile-page.png' });
  });
});
