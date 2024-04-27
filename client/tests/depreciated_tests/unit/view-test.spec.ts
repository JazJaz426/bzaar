import { describe, expect, test } from "vitest";
import { SharedState } from "../../../src/components/SharedState";

//documentation referenced: https://www.sitepoint.com/promises-in-javascript-unit-tests-the-definitive-guide/

/**
 * View after successful load
 */
test("View after successful load", async () => {
  let sharedStateInstance = new SharedState();
  let load_output = await sharedStateInstance.LoadFileCommand([
    "./data/server-data/city-town-income.csv",
  ]);
  await expect(load_output).toBe(
    "File ./data/server-data/city-town-income.csv loaded Successfully"
  );

  let view_output = await sharedStateInstance.ViewCommand([""]);
  await expect(view_output).toStrictEqual([
    [
      "City/Town",
      "Median Household Income",
      "Median Family Income",
      "Per Capita Income",
    ],
    ["Rhode Island", "74,489.00", "95,198.00", "39,603.00"],
    ["Barrington", "130,455.00", "154,441.00", "69,917.00"],
    ["Bristol", "80,727.00", "115,740.00", "42,658.00"],
    ["Burrillville", "96,824.00", "109,340.00", "39,470.00"],
    ["Central Falls", "40,235.00", "42,633.00", "17,962.00"],
    ["Charlestown", "86,023.00", "102,325.00", "50,086.00"],
    ["Coventry", "88,779.00", "104,685.00", "41,409.00"],
    ["Cranston", "77,145.00", "95,763.00", "38,269.00"],
    ["Cumberland", "104,613.00", "116,321.00", "46,179.00"],
    ["East Greenwich", "133,373.00", "173,775.00", "71,096.00"],
    ["East Providence", "65,016.00", "93,935.00", "38,714.00"],
    ["Exeter", "95,053.00", "116,894.00", "41,058.00"],
    ["Foster", "99,892.00", "118,000.00", "37,382.00"],
    ["Glocester", "97,753.00", "108,125.00", "39,743.00"],
    ["Hopkinton", "87,712.00", "103,393.00", "42,672.00"],
    ["Jamestown", "120,129.00", "156,465.00", "74,159.00"],
    ["Johnston", "75,579.00", "93,174.00", "36,251.00"],
    ["Lincoln", "94,571.00", "115,975.00", "44,135.00"],
    ["Little Compton", "96,111.00", "126,823.00", "81,912.00"],
    ["Middletown", "88,211.00", "104,953.00", "47,714.00"],
    ["Narragansett", "82,532.00", "124,830.00", "44,414.00"],
    ["New Shoreham", "72,279.00", "75,096.00", "37,067.00"],
    ["Newport", "77,092.00", "115,140.00", "48,803.00"],
    ["North Kingstown", "104,026.00", "126,663.00", "52,035.00"],
    ["North Providence", "68,821.00", "82,117.00", "35,843.00"],
    ["North Smithfield", "87,121.00", "108,906.00", "43,850.00"],
    ["Pawtucket", "56,427.00", "71,649.00", "30,246.00"],
    ["Portsmouth", "104,073.00", "134,442.00", "54,981.00"],
    ["Providence", "55,787.00", "65,461.00", "31,757.00"],
    ["Richmond", "100,493.00", "112,121.00", "44,904.00"],
    ["Scituate", "104,388.00", "117,740.00", "50,027.00"],
    ["Smithfield", "87,819.00", "111,767.00", "40,495.00"],
    ["South Kingstown", "102,242.00", "114,202.00", "42,080.00"],
    ["Tiverton", "85,522.00", "108,484.00", "44,202.00"],
    ["Warren", "75,755.00", "105,304.00", "42,683.00"],
    ["Warwick", "77,110.00", "97,033.00", "41,476.00"],
    ["West Greenwich", "126,402.00", "122,674.00", "44,457.00"],
    ["West Warwick", "62,649.00", "80,699.00", "36,148.00"],
    ["Westerly", "81,051.00", "107,013.00", "46,913.00"],
    ["Woonsocket", "48,822.00", "58,896.00", "26,561.00"],
  ]);
});

/**
 * View after empty load
 */
test("View after empty load", async () => {
  let sharedStateInstance = new SharedState();
  let load_output = await sharedStateInstance.LoadFileCommand([]);
  await expect(load_output).toBe(
    "Error: Invalid arguments for load command. Usage: load <filepath> or load <filepath> header"
  );

  let view_output = await sharedStateInstance.ViewCommand([""]);
  await expect(view_output).toStrictEqual("No file loaded");
});

/**
 * View before load
 */
test("View before load", async () => {
  let sharedStateInstance = new SharedState();
  let view_output = await sharedStateInstance.ViewCommand([
    "./data/server-data/city-town-income.csv",
  ]);
  await expect(view_output).toStrictEqual("No file loaded");
});

/**
 * View after failed load
 */
test("View after failed load", async () => {
  let sharedStateInstance = new SharedState();
  let load_output = await sharedStateInstance.LoadFileCommand(["hello"]);
  await expect(load_output).toBe(
    "Error happens when parsingjava.io.FileNotFoundException: hello (No such file or directory)"
  );

  let view_output = await sharedStateInstance.ViewCommand([""]);
  await expect(view_output).toStrictEqual("No file loaded");
});

/**
 * View after successful load then failed load
 */
test("View after successful load then failed load", async () => {
  let sharedStateInstance = new SharedState();
  let load_output = await sharedStateInstance.LoadFileCommand([
    "./data/census/postsecondary_education.csv",
  ]);
  await expect(load_output).toBe(
    "File ./data/census/postsecondary_education.csv loaded Successfully"
  );

  let view_output = await sharedStateInstance.ViewCommand([""]);
  await expect(view_output).toStrictEqual([
    [
      "IPEDS Race",
      "ID Year",
      "Year",
      "ID University",
      "University",
      "Completions",
      "Slug University",
      "share",
      "Sex",
      "ID Sex",
    ],
    [
      "Asian",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "214",
      "brown-university",
      "0.069233258",
      "Men",
      "1",
    ],
    [
      "Black or African American",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "77",
      "brown-university",
      "0.024911032",
      "Men",
      "1",
    ],
    [
      "Native Hawaiian or Other Pacific Islanders",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "3",
      "brown-university",
      "0.00097056",
      "Men",
      "1",
    ],
    [
      "Hispanic or Latino",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "143",
      "brown-university",
      "0.046263345",
      "Men",
      "1",
    ],
    [
      "Two or More Races",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "58",
      "brown-university",
      "0.018764154",
      "Men",
      "1",
    ],
    [
      "American Indian or Alaska Native",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "4",
      "brown-university",
      "0.00129408",
      "Men",
      "1",
    ],
    [
      "Non-resident Alien",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "327",
      "brown-university",
      "0.105791006",
      "Men",
      "1",
    ],
    [
      "White",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "691",
      "brown-university",
      "0.223552248",
      "Men",
      "1",
    ],
    [
      "Asian",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "235",
      "brown-university",
      "0.076027176",
      "Women",
      "2",
    ],
    [
      "Black or African American",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "95",
      "brown-university",
      "0.03073439",
      "Women",
      "2",
    ],
    [
      "Native Hawaiian or Other Pacific Islanders",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "4",
      "brown-university",
      "0.00129408",
      "Women",
      "2",
    ],
    [
      "Hispanic or Latino",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "207",
      "brown-university",
      "0.066968619",
      "Women",
      "2",
    ],
    [
      "Two or More Races",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "85",
      "brown-university",
      "0.027499191",
      "Women",
      "2",
    ],
    [
      "American Indian or Alaska Native",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "7",
      "brown-university",
      "0.002264639",
      "Women",
      "2",
    ],
    [
      "Non-resident Alien",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "281",
      "brown-university",
      "0.090909091",
      "Women",
      "2",
    ],
    [
      "White",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "660",
      "brown-university",
      "0.213523132",
      "Women",
      "2",
    ],
  ]);
  load_output = await sharedStateInstance.LoadFileCommand(["./badpath.csv"]);
  await expect(load_output).toBe(
    "Error happens when parsingjava.io.FileNotFoundException: ./badpath.csv (No such file or directory)"
  );

  view_output = await sharedStateInstance.ViewCommand([""]);
  await expect(view_output).toBe("No file loaded");
});

/**
 * View after two successful loads
 */
test("View after successful load then failed load", async () => {
  let sharedStateInstance = new SharedState();
  let load_output = await sharedStateInstance.LoadFileCommand([
    "./data/census/postsecondary_education.csv",
  ]);
  await expect(load_output).toBe(
    "File ./data/census/postsecondary_education.csv loaded Successfully"
  );

  let view_output = await sharedStateInstance.ViewCommand([""]);
  await expect(view_output).toStrictEqual([
    [
      "IPEDS Race",
      "ID Year",
      "Year",
      "ID University",
      "University",
      "Completions",
      "Slug University",
      "share",
      "Sex",
      "ID Sex",
    ],
    [
      "Asian",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "214",
      "brown-university",
      "0.069233258",
      "Men",
      "1",
    ],
    [
      "Black or African American",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "77",
      "brown-university",
      "0.024911032",
      "Men",
      "1",
    ],
    [
      "Native Hawaiian or Other Pacific Islanders",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "3",
      "brown-university",
      "0.00097056",
      "Men",
      "1",
    ],
    [
      "Hispanic or Latino",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "143",
      "brown-university",
      "0.046263345",
      "Men",
      "1",
    ],
    [
      "Two or More Races",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "58",
      "brown-university",
      "0.018764154",
      "Men",
      "1",
    ],
    [
      "American Indian or Alaska Native",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "4",
      "brown-university",
      "0.00129408",
      "Men",
      "1",
    ],
    [
      "Non-resident Alien",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "327",
      "brown-university",
      "0.105791006",
      "Men",
      "1",
    ],
    [
      "White",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "691",
      "brown-university",
      "0.223552248",
      "Men",
      "1",
    ],
    [
      "Asian",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "235",
      "brown-university",
      "0.076027176",
      "Women",
      "2",
    ],
    [
      "Black or African American",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "95",
      "brown-university",
      "0.03073439",
      "Women",
      "2",
    ],
    [
      "Native Hawaiian or Other Pacific Islanders",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "4",
      "brown-university",
      "0.00129408",
      "Women",
      "2",
    ],
    [
      "Hispanic or Latino",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "207",
      "brown-university",
      "0.066968619",
      "Women",
      "2",
    ],
    [
      "Two or More Races",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "85",
      "brown-university",
      "0.027499191",
      "Women",
      "2",
    ],
    [
      "American Indian or Alaska Native",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "7",
      "brown-university",
      "0.002264639",
      "Women",
      "2",
    ],
    [
      "Non-resident Alien",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "281",
      "brown-university",
      "0.090909091",
      "Women",
      "2",
    ],
    [
      "White",
      "2020",
      "2020",
      "217156",
      "Brown University",
      "660",
      "brown-university",
      "0.213523132",
      "Women",
      "2",
    ],
  ]);
  load_output = await sharedStateInstance.LoadFileCommand([
    "./data/census/dol_ri_earnings_disparity.csv",
  ]);
  await expect(load_output).toBe(
    "File ./data/census/dol_ri_earnings_disparity.csv loaded Successfully"
  );

  view_output = await sharedStateInstance.ViewCommand([""]);
  await expect(view_output).toStrictEqual([
    [
      "State",
      "Data Type",
      "Average Weekly Earnings",
      "Number of Workers",
      "Earnings Disparity",
      "Employed Percent",
    ],
    ["RI", "White", " $1,058.47 ", "395773.6521", "$1.00", "75%"],
    ["RI", "Black", "$770.26", "30424.80376", "$0.73", "6%"],
    [
      "RI",
      "Native American/American Indian",
      "$471.07",
      "2315.505646",
      "$0.45",
      "0%",
    ],
    [
      "RI",
      "Asian-Pacific Islander",
      " $1,080.09 ",
      "18956.71657",
      "$1.02",
      "4%",
    ],
    ["RI", "Hispanic/Latino", "$673.14", "74596.18851", "$0.64", "14%"],
    ["RI", "Multiracial", "$971.89", "8883.049171", "$0.92", "2%"],
  ]);
});
