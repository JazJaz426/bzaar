import { Dispatch, SetStateAction } from "react";
import "../styles/main.css";
import { REPLResult } from "./REPL";
import { sharedStateInstance } from "./REPLInput";
/**
 * Represents the props for the REPLHistory component.
 */
interface REPLHistoryProps {
  /**
   * An array of REPLResult objects representing the REPL output history.
   */
  listOfREPLResults: REPLResult[];
  /**
   * The display mode for the REPL history.
   */
  displayMode: string;
}

/**
 * A component that displays the REPL history.
 * @param props The REPLHistoryProps object containing the component's props.
 * @returns The REPLHistory component.
 */
export function REPLHistory(props: REPLHistoryProps) {
  return (
    <div className="repl-history">
      {props.listOfREPLResults.map((result, index) => {
        const commandDisplay = (
          <p aria-label="Inputted Command" key={`${index}-command`}>
            <strong>Command:</strong> {result.commandString}
          </p>
        );
        let outputDisplay;
        if (Array.isArray(result.output)) {
          outputDisplay = (
            <table aria-label="Outputted Table" key={`${index}-output`}>
              <tbody aria-label="table body">
                {result.output.map((subResult, subIndex) => (
                  <>
                    {subIndex === 0 && sharedStateInstance.getHasHeader() ? (
                      <tr key={`header-row-${subIndex}`}>
                        {subResult.map((subSubResult, subSubIndex) => (
                          <th
                            aria-label="table header"
                            key={`header-cell-${subIndex}-${subSubIndex}`}
                          >
                            {subSubResult}
                          </th>
                        ))}
                      </tr>
                    ) : (
                      <tr key={`row-${subIndex}`}>
                        {subResult.map((subSubResult, subSubIndex) => (
                          <td key={`cell-${subIndex}-${subSubIndex}`}>
                            {subSubResult}
                          </td>
                        ))}
                      </tr>
                    )}
                  </>
                ))}
              </tbody>
            </table>
          );
        } else {
          outputDisplay = (
            <p aria-label="Outputted Message" key={`${index}-output`}>
              <strong>Output:</strong> {result.output}
            </p>
          );
        }
        return (
          <div key={index}>
            {props.displayMode === "verbose" && commandDisplay}
            {outputDisplay}
          </div>
        );
      })}
    </div>
  );
}
