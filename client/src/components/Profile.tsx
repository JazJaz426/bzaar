import React, { useState, useEffect } from 'react';
import { getUserProfile } from '../utils/api'; // Import the new function
import { getLoginEmail } from '../utils/cookie';
import "../styles/profile.css";
import "../styles/main.css";

export default function Profile() {
  const [email, setEmail] = useState(getLoginEmail());
  const [name, setName] = useState('');
  const [address, setAddress] = useState('');

  useEffect(() => {
    const fetchProfile = async () => {
        let data;
        try {
            data = await getUserProfile(email);
            setEmail(data.email);  // Assuming data has an email property directly
            setName(data.name);
            setAddress(data.address);
        } catch (error) {
            console.error('Failed to fetch profile data:', error);
            alert('Failed to fetch profile data.');
        }
    };
    fetchProfile();
  }, []);
  
return (
  <div className="profile-content">
    <div className="profile-info">
      <div className="profile-section">  {/* Updated class name */}
        <strong>My Email: </strong>
        <span>{email}</span>
      </div>
      <div className="profile-section">  {/* Updated class name */}
        <strong>My Name: </strong>
        <span>{name}</span>
      </div>
      <div className="profile-section">  {/* Updated class name */}
        <strong>My Address: </strong>
        <span>{address}</span>
      </div>
    </div>
  </div>
);
}