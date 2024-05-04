
import "../styles/App.css";
import MainPage from "./MainPage";
import Discover from "./Discover";
import ItemDetail from './ItemDetails';
import WatchList from './WatchList';
import ClaimList from './ClaimList';
import Profile from './Profile';
import Layout from './Layout';
import Selling from './Selling';
import AuthRoute from "./auth/AuthRoute";
import ItemForm from './ItemForm';
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SearchPage from "./SearchPage";
import firebase from 'firebase/compat/app';

const firebaseConfig = {
  apiKey: process.env.API_KEY,
  authDomain: process.env.AUTH_DOMAIN,
  projectId: process.env.PROJECT_ID,
  storageBucket: process.env.STORAGE_BUCKET,
  messagingSenderId: process.env.MESSAGING_SENDER_ID,
  appId: process.env.APP_ID,
};

// Initialize Firebase
if (!firebase.apps.length) {
  firebase.initializeApp(firebaseConfig);
}

function App() {
  return (
    <div className="App">
      <Router>
        <Routes>
          <Route path="/" element={<AuthRoute gatedContent={<MainPage/>}/>} />
          <Route path="/discover" element={<Discover />} />
          <Route path="/search" element={<SearchPage />} />
          <Route path="/item-details/:id" element={<ItemDetail />} />
          <Route path="/my-listings" element={<Selling />} />
          <Route path="/post" element={<ItemForm />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/watchlist" element={<WatchList />} />
          <Route path="/claimlist" element={<ClaimList />} />
          {/* Add more routes as needed */}
        </Routes>
      </Router>
    </div>
  );
}

export default App;
