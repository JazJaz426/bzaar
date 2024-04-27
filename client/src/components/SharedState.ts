import { useState } from "react";
import { REPLFunction } from "./REPL";
import { mockedData, csvMap } from "../../data/mocked_data/mocked_data";
import { get } from "http";
import { log } from "console";

export class SharedState {
  private displayMode: string;
  private functionMap: Map<string, any>;
  private hasHeader: boolean;
  private fileLoaded: boolean;

  constructor() {
    this.displayMode = "brief";
    this.functionMap = new Map<string, any>(); // Initialize functionMap with an empty Map object
    this.hasHeader = false;
    this.fileLoaded = false;
    this.functionMap.set("mode", this.modeCommand);
    this.functionMap.set("load", this.LoadFileCommand);
    this.functionMap.set("view", this.ViewCommand);
    this.functionMap.set("search", this.SearchCommand);
    this.functionMap.set("broadband", this.BroadbandCommand);
  }
  public getHasHeader = () => {
    return this.hasHeader;
  };
  public getFunctionMap = () => {
    return this.functionMap;
  };
  public getDisplayMode = () => {
    return this.displayMode;
  };

  /**
   * Toggles the display mode between 'brief' and 'verbose'.
   *
   * This method is invoked to switch the display mode of the REPL output.
   * When the display mode is 'brief', it switches to 'verbose', and vice versa.
   *
   * @param args - An array of strings representing the arguments passed to the command.
   * @returns A string indicating the new display mode after the toggle.
   */

  public modeCommand = (args: Array<string>): string => {
    if (this.displayMode === "brief") {
      this.displayMode = "verbose";
      return "Switched to verbose mode";
    } else {
      this.displayMode = "brief";
      return "Switched to brief mode";
    }
  };
  /**
   * Loads a CSV file from a specified filepath from the server.
   *
   *
   * @param args - An array of strings representing the arguments passed to the command.
   *               The first argument is the filepath of the CSV file to be loaded.
   *               The second optional argument is "header" which indicates that the CSV file has a header row.
   * @returns A promise that resolves to a string indicating the result of the load operation.
   *          If the file is loaded successfully, it returns a success message.
   *          If the file fails to load, it returns an error message.
   */

  public LoadFileCommand: REPLFunction = async (
    args: Array<string>
  ): Promise<string | string[][]> => {
    try {
      let response;
      if (args.length === 1) {
        this.hasHeader = false;
        console.log(args);
        response = await fetch(
          `http://localhost:3232/loadcsv?filepath=${args[0]}`
        );
      } else if (args.length === 2 && args[1] === "header") {
        this.hasHeader = true;
        console.log("Header:", args[1]);
        response = await fetch(
          `http://localhost:3232/loadcsv?filepath=${args[0]}&headerFlag=true`
        );
      } else {
        this.fileLoaded = false;
        return `Error: Invalid arguments for load command. Usage: load <filepath> or load <filepath> header`;
      }
      const data = await response.json();
      if (data.result === "success") {
        this.fileLoaded = true;
        return `File ${args[0]} loaded Successfully`;
      } else {
        this.fileLoaded = false;
        console.log("Error loading file:", data.message);
        return data.message;
      }
    } catch (error) {
      this.fileLoaded = false;
      console.error("Error loading file:", error);
      return `Error: Failed to load data from ${args[0]}`;
    }
  };
  /**
   * Searches for data within the loaded CSV file based on the provided arguments.
   *
   * @param args - An array of strings representing the arguments passed to the command.
   *               The first argument specifies the search mode ("multi" for multi-value search) or the search value.
   *               The second argument (optional) specifies the column name for column-specific searches.
   * @returns A promise that resolves to a string indicating the result of the search operation.
   *          If matches are found, it returns the matching rows.
   *          If no matches are found, it returns a message indicating no match.
   *          If an error occurs, it returns an error message.
   */

  public SearchCommand: REPLFunction = async (
    args: Array<string>
  ): Promise<string | string[][]> => {
    if (args[0] === "multi") {
      if (args.length <= 1) {
        return `Error: No query provided for search multi`;
      }
      try {
        const response = await fetch(
          `http://localhost:3232/searchcsv?queries=${args[1]}&multi=true`
        );
        console.log(args[1]);
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        const data = await response.json();
        if (data.result === "success") {
          console.log("Output:", data.data);
          if (data.data.length === 0) {
            return `No match found`;
          }
          return data.data;
        } else {
          console.log("Error searching file:", data.message);
          return data.message;
        }
      } catch (error) {
        console.error("Error searching file:", error);
        return `Error: Failed to search data`;
      }
    } else {
      if (args.length === 0) {
        return `Error: No search val provided for search`;
      }
      let address;

      let searchStr = args.join("%20");
      if (searchStr.includes("|") === false) {
        address = `http://localhost:3232/searchcsv?val=${searchStr}`;
      } else {
        let searchVal = searchStr.split("|")[0];
        let searchCol = searchStr.split("|")[1];
        address = `http://localhost:3232/searchcsv?val=${searchVal}&col=${searchCol}`;
        console.log("address:", address);
      }

      try {
        const response = await fetch(address);
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        const data = await response.json();
        if (data.result === "success") {
          if (data.data.length === 0) {
            return `No match found`;
          }
          return data.data;
        } else {
          console.log("Error:", data);
          return data.message;
        }
      } catch (error) {
        console.error("Error searching file:", error);
        return `Error: Failed to search data from ${args[0]}`;
      }
    }
  };
  /**
   * Display the contents of the currently loaded CSV file.
   *
   * This command sends a request to the server to retrieve the contents of the loaded CSV file.
   * If a file is currently loaded, it returns the data from the file. Otherwise, it returns a message
   * indicating that no file is loaded.
   *
   * @returns A promise that resolves to the data from the loaded CSV file or a message indicating
   * that no file is loaded.
   */

  public ViewCommand: REPLFunction = async (
    args: Array<string>
  ): Promise<string | string[][]> => {
    try {
      const response = await fetch("http://localhost:3232/viewcsv");
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data = await response.json();
      if (data.result === "success") {
        if (this.fileLoaded) {
          return data.data;
        } else {
          return "No file loaded";
        }
      } else {
        console.log(data.result + ":", data.message);
        return data.message;
      }
    } catch (error) {
      console.error("Error loading file:", error);
      return `Error: Failed to view file`;
    }
  };
  /**
   * This command sends a request to the server with specified parameters to retrieve broadband data.
   * The parameters include state, county, and variables. If the variables parameter is not provided,
   * a default value is used. The server response is processed to format the broadband metrics, state name,
   * and county name into a readable string.
   *
   * @param args An array of strings representing the command arguments (state, county, and variables).
   * @returns A promise that resolves to a string containing the broadband data or an error message.
   */

  public BroadbandCommand: REPLFunction = async (
    args: Array<string>
  ): Promise<string | string[][]> => {
    args = args.map((arg) => arg.replace(/\+/g, " "));
    if (args[2] === undefined) {
      args[2] = "S2802_C03_022E";
    }
    const url = `http://localhost:3232/broadband?state=${args[0]}&county=${args[1]}&variables=${args[2]}`;
    console.log("broadband url:", url);
    try {
      const response = await fetch(url);
      console.log("response:", response);
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data = await response.json();
      if (data.result === "success") {
        let metrics = "";
        for (const [originalKey, value] of Object.entries(data.metrics)) {
          let key = originalKey;
          if (key === "S2802_C03_022E") {
            key = "Broadband Percentage";
          }
          metrics += key + ": " + value + ", ";
        }
        return (
          metrics +
          "state: " +
          data.stateName +
          ", " +
          "county: " +
          data.countyName
        );
      } else {
        console.log("Error calling broadband: ", data.error_message);
        return "Error calling broadband: " + data.error_message;
      }
    } catch (error) {
      console.error("Error calling broadband: ", error);
      return `Error calling broadband: ${error}`;
    }
  };
}
