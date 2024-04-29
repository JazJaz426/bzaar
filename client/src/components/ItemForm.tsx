import React, { useState } from 'react';
import '../styles/ItemForm.css';
import { db } from './firebase.js'; // Import Firestore database
import { collection, addDoc } from "firebase/firestore";

const ItemForm = () => {
    const [formData, setFormData] = useState({
        title: '',
        price: '',
        status: 'available',
        condition: '',
        description: '',
        image: null as File | null,
        category: 'furniture'
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const target = e.target as HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement;
        setFormData({ ...formData, [target.name]: target.value });
    };

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            setFormData({ ...formData, image: e.target.files[0] });
        }
    };

    // create
    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            // Add the form data to the Firestore 'items' collection
            const docRef = await addDoc(collection(db, "items"), formData);
            console.log("Document written with ID: ", docRef.id);
        } catch (e) {
            console.error("Error adding document: ", e);
        }
    };
    return (
        <div className="form-container">
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <input type="text" name="title" value={formData.title} onChange={handleChange} placeholder="Title" className="form-input" />
                <select name="category" value={formData.category} onChange={handleChange} className="form-select">
                    <option value="Furniture">furniture</option>
                    <option value="Clothing">clothing</option>
                    <option value="Kitchen">kitchen</option>
                    <option value="Others">others</option>
                </select>
                <input type="text" name="price" value={formData.price} onChange={handleChange} placeholder="Price" className="form-input" />
                <input type="text" name="condition" value={formData.condition} onChange={handleChange} placeholder="Condition" className="form-input" />
                <textarea name="description" value={formData.description} onChange={handleChange} placeholder="Description" className="form-textarea"></textarea>
                <input type="file" name="image" onChange={handleImageChange} className="form-input" />
                <button type="submit" className="form-button">Save</button>
            </form>
        </div>
    );
};
export default ItemForm;