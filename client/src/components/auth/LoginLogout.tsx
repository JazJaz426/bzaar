import { getAuth, GoogleAuthProvider, signInWithPopup } from "firebase/auth";
import React from "react";
import { getUserProfile } from '../../utils/api';
import { addLoginId, removeLoginId, addLoginEmail, removeLoginEmail, addUserId, removeUserId } from "../../utils/cookie";
import "../../styles/LoginLogout.css";
export interface ILoginPageProps {
  loggedIn: boolean;
  setLogin: React.Dispatch<React.SetStateAction<boolean>>;
}

const Login: React.FunctionComponent<ILoginPageProps> = (props) => {
  const auth = getAuth();

  const signInWithGoogle = async () => {
    try {
      const response = await signInWithPopup(auth, new GoogleAuthProvider());
      const userEmail = response.user.email || "";
      // Check if the email ends with the allowed domain
      if (userEmail.endsWith("@brown.edu")) {
        // add unique user id as a cookie to the browser.
        addLoginId(response.user.uid);
        addLoginEmail(userEmail);
        props.setLogin(true);

        // Fetch user profile and store userId
        const userProfile = await getUserProfile(userEmail);
        if (userProfile && userProfile.data && userProfile.data.userId) {
          addUserId(userProfile.data.userId);
        }
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
      <img src="../assets/login_bear.png" className="login-bear"/>
      <button
        className="google-login-button"
        onClick={() => signInWithGoogle()}
        disabled={props.loggedIn}
      >
        Sign in with Brown Gmail Account
      </button>
    </div>
  );
};

const Logout: React.FunctionComponent<ILoginPageProps> = (props) => {
  const signOut = () => {
    removeLoginId();
    removeLoginEmail();
    removeUserId();
    props.setLogin(false);
  };

  return (
    <div className="logout-box">
      <button className="SignOut" onClick={() => signOut()}>
        Sign Out
      </button>
    </div>
  );
};

const LoginLogout: React.FunctionComponent<ILoginPageProps> = (props) => {
  return <>{!props.loggedIn ? <Login {...props} /> : <Logout {...props} />}</>;
};

export default LoginLogout;
