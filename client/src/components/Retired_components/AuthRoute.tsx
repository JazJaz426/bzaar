import { useState } from "react";
import LoginLogout from "./LoginLogout";
import REPL from "../REPL";

function AuthRoute() {
  const [authing, setAuthing] = useState(false);

  // USEFUL FOR PLAYWRIGHT TESTING PURPOSES: auto sets authing to true in test environment
  if (!authing && import.meta.env.VITE_APP_NODE_ENV === "test") {
    setAuthing(true);
  }

  return (
    <>
      <LoginLogout authing={authing} setAuthing={setAuthing} />
      {authing ? <REPL /> : null}
    </>
  );
}

export default AuthRoute;
