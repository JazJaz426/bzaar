
import "../styles/App.css";
import MapsGearup from "./MapsGearup";
import MainPage from "./MainPage";
import AuthRoute from "./auth/AuthRoute";



const firebaseConfig = {
  apiKey: process.env.API_KEY,
  authDomain: process.env.AUTH_DOMAIN,
  projectId: process.env.PROJECT_ID,
  storageBucket: process.env.STORAGE_BUCKET,
  messagingSenderId: process.env.MESSAGING_SENDER_ID,
  appId: process.env.APP_ID,
};



function App() {
  return (
    <div className="App">
      <AuthRoute gatedContent={<MainPage />} />
    </div>
  );
}

export default App;
