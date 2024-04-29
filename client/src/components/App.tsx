
import "../styles/App.css";
import MainPage from "./MainPage";
import ItemDetail from './ItemDetails';
import AuthRoute from "./auth/AuthRoute";
import ItemForm from './ItemForm';
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

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
      <Router>
        <Routes>
          <Route path="/" element={<AuthRoute gatedContent={<MainPage />} />} />
          <Route path="/item-details/:id" element={<ItemDetail />} />
          <Route path="/post" element={<ItemForm />} />
          {/* Add more routes as needed */}
        </Routes>
      </Router>
    </div>
  );
}

export default App;
