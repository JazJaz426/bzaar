import React, { useState, useEffect } from 'react';
import { getLoginCookie } from '../utils/cookie'; // Import the function to get cookie
import "../styles/profile.css";
import "../styles/main.css";

export default function Profile() {
  const [email, setEmail] = useState(localStorage.getItem('email'));
  const [name, setName] = useState('');
  const [address, setAddress] = useState('');

  
  useEffect(() => {
    const fetchProfile = async () => {
        let response;
        try {
            response = await fetch(`http://localhost:3232/getUserProfile?email=${email}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (!response.ok) {
                throw new Error(`Failed to fetch: ${response.status} ${response.statusText}`);
            }
            const data = await response.json();
            setEmail(data.email);
            setName(data.name);
            setAddress(data.address);
        } catch (error) {
            console.error('Failed to fetch profile data:', error);
            alert('Failed to fetch profile data.');
        }
    };
    if (email) {
        fetchProfile();
    }
}, [email]); // Dependency array
  
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