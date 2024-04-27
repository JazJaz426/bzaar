import { test, expect } from "@playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:8000/");
  // await page.getByLabel('Login').click();
});

test("e2e test", async ({ page }) => {
  // Your test code here
  await page.getByPlaceholder("Enter command here!").fill("broadband xxx");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: Error calling broadband: Provided state name 'xxx' doesn't exist"
    )
  ).toBeVisible();

  //command with no variable provided
  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: Broadband Percentage: 85.4, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();

  //enter variable for broadband explicitly
  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County S2802_C03_022E");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: Broadband Percentage: 85.4, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();

  //enter another variable
  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County SUMLEVEL");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: SUMLEVEL: 050, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();

  //enter multiple variable
  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County SUMLEVEL,S2802_C03_022E");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: SUMLEVEL: 050, Broadband Percentage: 85.4, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();

  //enter invalid variable
  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County xxxx,SUMLEVEL");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: SUMLEVEL: 050, xxxx: error: failed to retrieve corresponding data from the ACS API for the given location, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();
  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County xxxx");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: xxxx: error: failed to retrieve corresponding data from the ACS API for the given location, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();
});

test("e2e test with mocked backend, multiple variables", async ({ page }) => {
  // Intercept and mock the network request
  await page.route(
    "http://localhost:3232/broadband?state=Rhode%20Island&county=Providence%20County&variables=S2802_C03_022E",
    (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "success",
          metrics: { S2802_C03_022E: "85.4", SUMLEVEL: "050" },
          stateName: "Rhode Island",
          countyName: "Providence County",
        }),
      });
    }
  );

  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County SUMLEVEL,S2802_C03_022E");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: SUMLEVEL: 050, Broadband Percentage: 85.4, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();
});

test("e2e test with mocked backend, wrong command", async ({ page }) => {
  // Intercept and mock the network request
  await page.route(
    "http://localhost:3232/broadband?state=xxx&county=undefined&variables=S2802_C03_022E",
    (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "error_datasource",
          error_message: "Provided state name 'xxx' doesn't exist",
        }),
      });
    }
  );

  await page.getByPlaceholder("Enter command here!").fill("broadband xxx");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: Error calling broadband: Provided state name 'xxx' doesn't exist"
    )
  ).toBeVisible();
});

test("e2e test with mocked backend, command with no variable provided", async ({
  page,
}) => {
  // Intercept and mock the network request
  await page.route(
    "http://localhost:3232/broadband?state=Rhode%20Island&county=Providence%20County&variables=S2802_C03_022E",
    (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "success",
          metrics: { S2802_C03_022E: "85.4" },
          stateName: "Rhode Island",
          countyName: "Providence County",
        }),
      });
    }
  );

  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: Broadband Percentage: 85.4, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();
});

test("e2e test with mocked backend, command with explicit variable provided", async ({
  page,
}) => {
  // Intercept and mock the network request
  await page.route(
    "http://localhost:3232/broadband?state=Rhode%20Island&county=Providence%20County&variables=S2802_C03_022E",
    (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "success",
          metrics: { S2802_C03_022E: "85.4" },
          stateName: "Rhode Island",
          countyName: "Providence County",
        }),
      });
    }
  );

  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County S2802_C03_022E");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: Broadband Percentage: 85.4, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();
});

test("e2e test with mocked backend, command with another variable provided", async ({
  page,
}) => {
  // Intercept and mock the network request
  await page.route(
    "http://localhost:3232/broadband?state=Rhode%20Island&county=Providence%20County&variables=SUMLEVEL",
    (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "success",
          metrics: { SUMLEVEL: "050" },
          stateName: "Rhode Island",
          countyName: "Providence County",
        }),
      });
    }
  );

  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County SUMLEVEL");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: SUMLEVEL: 050, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();
});

test("e2e test with mocked backend, enter invalid variable", async ({
  page,
}) => {
  await page.route(
    "http://localhost:3232/broadband?state=Rhode+Island&county=Providence+County&variables=xxxx",
    (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "success",
          metrics: {
            xxxx: "error: failed to retrieve corresponding data from the ACS API for the given location",
          },
          stateName: "Rhode Island",
          countyName: "Providence County",
        }),
      });
    }
  );

  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County xxxx");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: xxxx: error: failed to retrieve corresponding data from the ACS API for the given location, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();
});

test("e2e test with mocked backend, enter invalid variable with valid variable", async ({
  page,
}) => {
  // Intercept and mock the network request for a mix of valid and invalid variables
  await page.route(
    "http://localhost:3232/broadband?state=Rhode+Island&county=Providence+County&variables=xxxx,SUMLEVEL",
    (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "success",
          metrics: {
            xxxx: "error: failed to retrieve corresponding data from the ACS API for the given location",
            SUMLEVEL: "050",
          },
          stateName: "Rhode Island",
          countyName: "Providence County",
        }),
      });
    }
  );

  await page
    .getByPlaceholder("Enter command here!")
    .fill("broadband Rhode+Island Providence+County xxxx,SUMLEVEL");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: SUMLEVEL: 050, xxxx: error: failed to retrieve corresponding data from the ACS API for the given location, state: Rhode Island, county: Providence County"
    )
  ).toBeVisible();
});
