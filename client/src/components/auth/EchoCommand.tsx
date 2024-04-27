import { REPLFunction } from "../REPL";

/**
 * test function for practice, can be used as a stencil for other classes
 *
 * A command-processor function for our REPL. The function returns a string to print to
 * history when the command is done executing.
 *
 */

export const EchoCommand: REPLFunction = (args: Array<string>): string => {
  return args.join(" ");
};
