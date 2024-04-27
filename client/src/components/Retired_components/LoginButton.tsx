import { Dispatch, SetStateAction } from 'react';

/**
 * Represents the props for the LoginButton component.
 */
interface loginProps {
  isLoggedIn: boolean;
  setIsLoggedIn: Dispatch<SetStateAction<boolean>>;
}

/**
 * Renders a login button that toggles the login state.
 * @param props - The loginProps object containing the isLoggedIn state and setIsLoggedIn function.
 * @returns A button component that displays "Sign out" if the user is logged in, and "Login" otherwise.
 */
export function LoginButton(props: loginProps) {

  /**
   * Toggles the login state and updates the isLoggedIn value.
   * @returns The new value of isLoggedIn after toggling.
   */
  const authenticate = () => {
    const newValue = !props.isLoggedIn;
    props.setIsLoggedIn(newValue);
    return newValue;
  }

  if (props.isLoggedIn) {
    return (
      <button aria-label='Sign Out' onClick={authenticate}>Sign out</button>
    )
  } else {
    return (
      <button aria-label='Login' onClick={authenticate}>Login</button>
    )
  }
}