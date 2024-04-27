import "../styles/main.css";
import { Dispatch, SetStateAction, useState, useEffect } from "react";
import { ControlledInput } from "./ControlledInput";
import { REPLResult } from "./REPL";
import { SharedState } from "./SharedState";
import { get } from "http";

/**
 * Represents the input component for the REPL interface.
 *
 * This component handles the input of commands from the user, processes them, and calls the function via the FunctionMap.
 * It also listens for specific key strokes (Ctrl+M and Ctrl+V) to handle keyboard calls to view and mode.
 *
 * Props:
 * - loadFileName: The name of the file being loaded.
 * - setLoadFileName: Function to set the name of the file being loaded.
 * - commandString: The current command string input by the user.
 * - setCommandString: Function to set the current command string.
 * - index: The current command index.
 * - setIndex: Function to set the current command index.
 * - displayMode: The current display mode of the REPL output.
 * - setDisplayMode: Function to set the current display mode.
 * - listOfREPLResults: The list of results from executed REPL commands.
 * - setListOfREPLResults: Function to update the list of results.
 *
 * The component utilizes the `SharedState` instance to access and modify the global state shared across components.
 */

export const sharedStateInstance = new SharedState();

export interface REPLInputProps {
  // TODO: Fill this with desired props... Maybe something to keep track of the submitted commands
  loadFileName: string;
  setLoadFileName: Dispatch<SetStateAction<string>>;
  commandString: string;
  setCommandString: Dispatch<SetStateAction<string>>;
  index: number;
  setIndex: Dispatch<SetStateAction<number>>;
  displayMode: string;
  setDisplayMode: Dispatch<SetStateAction<string>>;
  listOfREPLResults: REPLResult[];
  setListOfREPLResults: Dispatch<SetStateAction<REPLResult[]>>;
}
/**
 *  *
 * This function takes in props related to the REPL state, such as the current command string,
 * the index of the current command, and the list of previous REPL results. It also handles the execution of commands
 * by listening for specific key strokes (Ctrl+M and Ctrl+V) to handle keyboard calls to view and mode.
 *
 * @param props - The REPLInputProps object containing various states and state setter functions related to the REPL.
 * @returns A JSX element that renders the input component for the REPL interface.
 */

export function REPLInput(props: REPLInputProps) {
  const functionMap = sharedStateInstance.getFunctionMap();

  useEffect(() => {
    const handleGlobalKeyPress = (event: KeyboardEvent) => {
      if (event.ctrlKey && event.key === "m") {
        console.log("key stroke m!");
        if (functionMap.has("mode")) {
          getResponse("mode-keystroke", []);
        }
      } else if (event.ctrlKey && event.key === "v") {
        console.log("key stroke v!");
        if (functionMap.has("view")) {
          getResponse("view-keystroke", []);
        }
      }
    };

    // Attach the event listener to the window object
    window.addEventListener("keydown", handleGlobalKeyPress);

    // Return a cleanup function to remove the event listener when the component re-renders
    return () => {
      window.removeEventListener("keydown", handleGlobalKeyPress);
    };
  }, [props, functionMap]); // Add dependencies that should re-attach the event listener when changed

  const getResponse = async (keyword: string, args: Array<string>) => {
    let output;
    if (keyword === "view-keystroke" || keyword === "mode-keystroke") {
      let newKeyword = keyword.split("-")[0];
      output = await functionMap.get(newKeyword)(args);
    } else {
      output = await functionMap.get(keyword)(args);
    }
    let newREPLResult;
    if (keyword === "view-keystroke" || keyword === "mode-keystroke") {
      newREPLResult = {
        commandString: "keystroke: " + keyword.split("-")[0],
        output: output, // Use the output obtained from executing the command
        index: props.index,
      };
    } else {
      newREPLResult = {
        commandString: props.commandString,
        output: output, // Use the output obtained from executing the command
        index: props.index,
      };
    }
    props.setDisplayMode(sharedStateInstance.getDisplayMode());
    props.setListOfREPLResults([...props.listOfREPLResults, newREPLResult]);
  };

  function handleClick() {
    processCommand();
  }
  function handleSubmit(event: React.KeyboardEvent) {
    // Check if Ctrl+Enter was pressed
    console.log("handling submit with enter!");
    if (event.key === "Enter") {
      processCommand();
    }
  }

  function processCommand() {
    const keyword = props.commandString.split(" ")[0];
    const args = props.commandString.split(" ").slice(1);
    let output;

    if (functionMap.has(keyword)) {
      getResponse(keyword, args);
    } else {
      output = "ERROR: Unknown command";
      const newREPLResult = {
        commandString: props.commandString,
        output: output, // Use the output obtained from executing the command
        index: props.index,
      };
      props.setListOfREPLResults([...props.listOfREPLResults, newREPLResult]);
    }

    // Reset the command string
    props.setCommandString("");
  }

  return (
    <div className="repl-input">
      <fieldset>
        <legend>Enter a command:</legend>
        <ControlledInput
          value={props.commandString}
          setValue={props.setCommandString}
          ariaLabel={"Command input"}
          onKeyDown={handleSubmit}
        />
      </fieldset>
      <button onClick={handleClick} aria-label="Submit">
        Submit{" "}
      </button>
    </div>
  );
}
