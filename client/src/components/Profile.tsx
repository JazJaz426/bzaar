import React, { useState, useEffect } from 'react';
import { getUserProfile, updateUserProfile } from '../utils/api'; // Import the new function
import { getLoginEmail } from '../utils/cookie';
import "../styles/profile.css";
import "../styles/main.css";

export default function Profile() {
  const [email, setEmail] = useState(getLoginEmail());
  const [name, setName] = useState('');
  const [address, setAddress] = useState('');
  const [editMode, setEditMode] = useState(false);

  useEffect(() => {
    const fetchProfile = async () => {
        let data;
        try {
            const userResponseMap = await getUserProfile(email);
            const userData = userResponseMap.data
            setEmail(userData.email);  // Assuming data has an email property directly
            setName(userData.name);
            setAddress(userData.address);
        } catch (error) {
            console.error('Failed to fetch profile data:', error);
            alert('Failed to fetch profile data.');
        }
    };
    fetchProfile();
  }, []);

  const handleUpdate = async () => {
    try {
      await updateUserProfile(name, email, address);
      alert('Profile updated successfully');
      setEditMode(false);
    } catch (error) {
      console.error('Failed to update profile:', error);
      alert('Failed to update profile.');
    }
  };

return (
  <div className="profile-content">
    {editMode ? (
      <>
        <input 
          value={name} 
          onChange={(e) => setName(e.target.value)} 
          placeholder="Name" 
          className="profile-input"
        />
        <input 
          value={address} 
          onChange={(e) => setAddress(e.target.value)} 
          placeholder="Address" 
          className="profile-input"
        />
        <button onClick={handleUpdate} className="profile-button">Update Profile</button>
        <button onClick={() => setEditMode(false)} className="profile-button">Cancel</button>
      </>
    ) : (
      <>
        <p className="profile-name">{name}</p>
        <div className="profile-section">
          <strong>My Email: </strong><span>{email}</span>
        </div>
        <div className="profile-section">
          <strong>Pickup Address: </strong><span>{address}</span>
        </div>
        <button onClick={() => setEditMode(true)} className="profile-button">Edit Profile</button>
      </>
    )}
  </div>
);
}
