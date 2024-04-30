import React, { useState } from 'react';
import '../styles/ItemForm.css';

import { collection, addDoc } from "firebase/firestore";
interface FormData {
    title: string;
    price: string;
    status: string;
    condition: string;
    description: string;
    images: File[] | null;
    category: string;

}
const ItemForm = () => {
    const [formData, setFormData] = useState({
        title: '',
        price: '',
        status: 'available',
        condition: '',
        description: '',
        images: [] as File[] | null,
        category: 'furniture'
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const target = e.target as HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement;
        setFormData({ ...formData, [target.name]: target.value });
    };

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            const imagesArray = Array.from(e.target.files);
            setFormData({ ...formData, images: imagesArray });
        }
    };

    // create
    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        console.log(formData);
        // const PostItem = async () => {
        let response;
        try {
            response = await fetch(`http://localhost:3232/postItem`, {
                method: 'POST',
                body: formData,
            });
            if (!response.ok) {
                throw new Error(`Failed to fetch: ${response.status} ${response.statusText}`);
            }
            const data = await response.json();
            if (data.status === '500') {
                console.error('Failed to fetch profile data:', data.message);
                throw new Error(data.error);
            }
        } catch (error) {
            console.error('Failed to fetch profile data:', error);
            alert('Failed to fetch profile data.');
        }
        // };
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
                <input type="file" name="image" onChange={handleImageChange} className="form-input" multiple />
                <button type="submit" className="form-button">Save</button>
            </form>
        </div>
    );
};
export default ItemForm;