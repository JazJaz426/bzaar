import "../styles/main.css";
import { Dispatch, SetStateAction } from "react";

interface ControlledInputProps {
  value: string;
  setValue: Dispatch<SetStateAction<string>>;
  ariaLabel: string;
  onKeyDown?: (event: React.KeyboardEvent) => void;
}

export function ControlledInput({
  value,
  setValue,
  ariaLabel,
  onKeyDown,
}: ControlledInputProps) {
  return (
    <input
      type="text"
      className="repl-command-box"
      value={value}
      placeholder="Enter command here!"
      onChange={(ev) => setValue(ev.target.value)}
      aria-label={ariaLabel}
      onKeyDown={onKeyDown} // Pass the onKeyDown prop to the input element
      tabIndex={1}
    />
  );
}
