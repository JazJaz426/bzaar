import { expect, test } from "@playwright/test";

// TA example:
// test("view after successful load with headers", async ({ page }) => {

//     await page.route(URLToIntercept, async (route) => {
//     console.log("Intercepted request:", route.request().url());

//     // Replace the response with the modified data
//     route.fulfill({
//         status: 200,
//         contentType: "application/json",
//         body: JSON.stringify(mockedDataResponse),
//     });
// });
// }

/**
 * View after successful load
 */
test("view after successful load", async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await page.route(
    "http://localhost:3232/loadcsv?filepath=./data/server-data/city-town-income.csv",
    async (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "success",
          message:
            "Successfully loaded file at ./data/server-data/city-town-income.csv",
          data: [
            ["City/Town", "Median Household Income", "Median Family Income"],
            ["City/Town", "Median Household Income", "Median Family Income"],
          ],
        }),
      });
    }
  );
  await page
    .getByLabel("Command input")
    .fill("load ./data/server-data/city-town-income.csv");
  await page.getByLabel("Submit").click();
  const output = await page
    .getByLabel("Outputted Message")
    .getByText(
      "Output: File ./data/server-data/city-town-income.csv loaded Successfully"
    );
  await expect(output).toBeVisible();

  await page.route("http://localhost:3232/viewcsv", async (route) => {
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        result: "success",
        data: [
          ["City/Town", "Median Household Income", "Median Family Income"],
        ],
      }),
    });
  });
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("Submit").click();
  let table = await page.getByRole("table");
  await expect(table).toContainText("City/Town");
});

test("view before load", async ({ page }) => {
  //const url = route.request().url();
  await page.goto("http://localhost:8000/");

  await page.route("http://localhost:3232/viewcsv", async (route) => {
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ message: "No file loaded" }),
    });
  });
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("Submit").click();
  await expect(page.getByText("Output: No file loaded")).toBeVisible();
});

/**
 * View after empty load
 */
test("view after empty load", async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await page.route("http://localhost:3232/loadcsv?filepath=", async (route) => {
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        result: "failure",
      }),
    });
  });
  await page.getByLabel("Command input").fill("load");
  await page.getByLabel("Submit").click();
  const output = await page
    .getByLabel("Outputted Message")
    .getByText(
      "Output: Error: Invalid arguments for load command. Usage: load <filepath> or load <filepath> header"
    );
  await expect(output).toBeVisible();

  await page.route("http://localhost:3232/viewcsv", async (route) => {
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        result: "fail",
        message: "No file loaded",
        data: [
          ["City/Town", "Median Household Income", "Median Family Income"],
        ],
      }),
    });
  });
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("Submit").click();
  await expect(page.getByText("Output: No file loaded")).toBeVisible();
});

/**
 * View after failed load
 */
test("view after failed load", async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await page.route(
    "http://localhost:3232/loadcsv?filepath=/hello.csv",
    async (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          message: "Invalid filepath",
        }),
      });
    }
  );
  await page.getByLabel("Command input").fill("load /hello.csv");
  await page.getByLabel("Submit").click();
  await expect(page.getByText("Output: Invalid filepath")).toBeVisible();

  await page.route("http://localhost:3232/viewcsv", async (route) => {
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        result: "fail",
        message: "No file loaded",
        data: [
          ["City/Town", "Median Household Income", "Median Family Income"],
        ],
      }),
    });
  });
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("Submit").click();
  await expect(page.getByText("Output: No file loaded")).toBeVisible();
});

/**
 * View after successful load then failed load
 */
test("view after successful load then failed load", async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await page.route(
    "http://localhost:3232/loadcsv?filepath=./charlie/chocolate/factory.csv",
    async (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "success",
          message:
            "Successfully loaded file at ./charlie/chocolate/factory.csv",
          data: [
            ["Good", "Morning", "Starshine"],
            ["The", "Earth", "Says", "Hello"],
          ],
        }),
      });
    }
  );
  await page
    .getByLabel("Command input")
    .fill("load ./charlie/chocolate/factory.csv");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText(
      "Output: File ./charlie/chocolate/factory.csv loaded Successfully"
    )
  ).toBeVisible();
  await page.route(
    "http://localhost:3232/loadcsv?filepath=./bad/path.csv",
    async (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "fail",
          message: "Invalid filepath",
        }),
      });
    }
  );
  await page.getByLabel("Command input").fill("load ./bad/path.csv");
  await page.getByLabel("Submit").click();
  await expect(page.getByText("Output: Invalid filepath")).toBeVisible();

  await page.route("http://localhost:3232/viewcsv", async (route) => {
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        result: "fail",
        message: "No file loaded",
      }),
    });
  });
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("Submit").click();
  await expect(page.getByText("Output: No file loaded")).toBeVisible();
  let table = await page.getByRole("table");
  await expect(table).not.toBeVisible();
});

/**
 * View after 2 successful loads
 */
test("view after two successful loads", async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await page.route(
    "http://localhost:3232/loadcsv?filepath=./The/Hunger/Games.csv",
    async (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "success",
          message: "Successfully loaded file at ./The/Hunger/Games.csv",
          data: [["Katniss", "Peeta", "Everdeen"]],
        }),
      });
    }
  );
  await page.getByLabel("Command input").fill("load ./The/Hunger/Games.csv");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText("Output: File ./The/Hunger/Games.csv loaded Successfully")
  ).toBeVisible();
  await page.route(
    "http://localhost:3232/loadcsv?filepath=./Harry/Potter.csv&headerFlag=true",
    async (route) => {
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          result: "success",
          message: "Successfully loaded file at ./Harry/Potter.csv",
          data: [["Ron", "Hermoine", "Voldemort"]],
        }),
      });
    }
  );
  await page.getByLabel("Command input").fill("load ./Harry/Potter.csv header");
  await page.getByLabel("Submit").click();
  await expect(
    page.getByText("Output: File ./Harry/Potter.csv loaded Successfully")
  ).toBeVisible();
  await page.route("http://localhost:3232/viewcsv", async (route) => {
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        result: "success",
        data: [["Ron", "Hermione", "Voldemort"]],
      }),
    });
  });
  await page.getByLabel("Command input").fill("view");
  await page.getByLabel("Submit").click();

  let table_header = await page.getByRole("table").getByLabel("table header");
  await expect(table_header.getByText("Hermione")).toBeVisible();
});
