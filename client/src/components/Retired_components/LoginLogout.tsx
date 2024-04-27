import React from "react";
import { getAuth, GoogleAuthProvider, signInWithPopup } from "firebase/auth";

export interface ILoginPageProps {
  authing: boolean;
  setAuthing: React.Dispatch<React.SetStateAction<boolean>>;
}
/**
 * Login component that provides a Google sign-in option.
 * It checks if the signed-in user's email ends with "@brown.edu".
 * If not, it signs the user out and logs a message indicating the user is not allowed.
 *
 * Returns a button that triggers the Google sign-in process when clicked.
 */

const Login: React.FunctionComponent<ILoginPageProps> = (props) => {
  const auth = getAuth();

  const signInWithGoogle = async () => {
    try {
      const response = await signInWithPopup(auth, new GoogleAuthProvider());
      const userEmail = response.user.email || "";

      // Check if the email ends with the allowed domain
      if (userEmail.endsWith("@brown.edu")) {
        console.log(response.user.uid);
        props.setAuthing(true);
      } else {
        // User is not allowed, sign them out and show a message
        await auth.signOut();
        console.log("User not allowed. Signed out.");
      }
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="login-box">
      <h1>Login Page</h1>
      <button
        className="google-login-button"
        aria-label={"google login button"}
        onClick={() => signInWithGoogle()}
        disabled={props.authing}
      >
        Sign in with Google
      </button>
    </div>
  );
};

/**
 * Logout component that provides a sign-out option.
 *
 * Returns a button that signs the user out when clicked.
 */

const Logout: React.FunctionComponent<ILoginPageProps> = (props) => {
  return (
    <div className="logout-box">
      <button
        className="SignOut"
        onClick={() => {
          props.setAuthing(false);
        }}
      >
        Sign Out
      </button>
    </div>
  );
};

const LoginLogout: React.FunctionComponent<ILoginPageProps> = (props) => {
  return <>{!props.authing ? <Login {...props} /> : <Logout {...props} />}</>;
};

export default LoginLogout;
