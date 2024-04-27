import { describe, it, expect, beforeEach, vi } from "vitest";
import { SharedState } from "../../src/components/SharedState";

// Mocking the global fetch function
global.fetch = vi.fn(() =>
  Promise.resolve({
    ok: true,
    json: () => Promise.resolve({ result: "success" }),
  })
) as unknown as typeof fetch;

describe("SharedState", () => {
  let sharedState: SharedState;

  beforeEach(() => {
    // Reset the mock and initialize SharedState before each test
    vi.clearAllMocks();
    sharedState = new SharedState();
  });

  it("LoadFileCommand successfully loads a file", async () => {
    const filepath = "data/census/income_by_race.csv";
    const result = await sharedState.LoadFileCommand([filepath]);

    expect(fetch).toHaveBeenCalledTimes(1);
    expect(fetch).toHaveBeenCalledWith(
      `http://localhost:3232/loadcsv?filepath=${filepath}`
    );
    expect(result).toEqual(`File ${filepath} loaded Successfully`);
    expect(sharedState.getHasHeader()).toBe(false);
  });

  it("LoadFileCommand fails to load a file with invalid path", async () => {
    // Mocking fetch to simulate a failed load operation
    (fetch as vi.Mock).mockImplementationOnce(() =>
      Promise.resolve({
        ok: false,
        json: () =>
          Promise.resolve({ result: "error", message: "File not found" }),
      })
    );

    const filepath = "invalid/path.csv";
    const result = await sharedState.LoadFileCommand([filepath]);

    expect(fetch).toHaveBeenCalledTimes(1);
    expect(fetch).toHaveBeenCalledWith(
      `http://localhost:3232/loadcsv?filepath=${filepath}`
    );
    expect(result).toContain("File not found");
  });

  it("LoadFileCommand successfully loads a file with header", async () => {
    const filepath = "data/census/income_by_race.csv";
    const result = await sharedState.LoadFileCommand([filepath, "header"]);

    expect(fetch).toHaveBeenCalledTimes(1);
    expect(fetch).toHaveBeenCalledWith(
      `http://localhost:3232/loadcsv?filepath=${filepath}&headerFlag=true`
    );
    expect(result).toEqual(`File ${filepath} loaded Successfully`);
    expect(sharedState.getHasHeader()).toBe(true);
  });

  it("LoadFileCommand with no args", async () => {
    const result = await sharedState.LoadFileCommand([]);

    expect(result).toEqual(
      `Error: Invalid arguments for load command. Usage: load <filepath> or load <filepath> header`
    );
  });

  it("LoadFileCommand handles network failure gracefully", async () => {
    const filepath = "data/test.csv";
    // Simulate a network failure by rejecting the fetch promise
    (fetch as vi.Mock).mockRejectedValue(new Error("Network Error"));

    const result = await sharedState.LoadFileCommand([filepath]);

    expect(fetch).toHaveBeenCalledTimes(1);
    expect(fetch).toHaveBeenCalledWith(
      `http://localhost:3232/loadcsv?filepath=${filepath}`
    );
    expect(result).toContain("Error: Failed to load data from");
    expect(sharedState.getHasHeader()).toBe(false);
  });
});
