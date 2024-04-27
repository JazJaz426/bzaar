/*
  Demo: test ordinary Java/TypeScript
*/

import { describe, expect, test } from "vitest";
import { SharedState } from "../../src/components/SharedState";

// For more information on how to make unit tests, visit:
// https://jestjs.io/docs/using-matchers

test("ModeCommand switches to verbose/brief/verbose", () => {
  let sharedStateInstance = new SharedState();
  var output1 = sharedStateInstance.modeCommand(["verbose"]);
  expect(output1).toEqual("Switched to verbose mode");
  var output2 = sharedStateInstance.modeCommand(["brief"]);
  expect(output2).toEqual("Switched to brief mode");
  var output3 = sharedStateInstance.modeCommand(["verbose"]);
  expect(output3).toEqual("Switched to verbose mode");
});
