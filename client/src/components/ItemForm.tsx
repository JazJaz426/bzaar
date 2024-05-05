import React, { useState } from 'react';
import '../styles/ItemForm.css';
import { getLoginId, getUserId } from '../utils/cookie';
import { collection, addDoc } from "firebase/firestore";
import { error } from 'console';
import { messagePopup, showErrorPopup } from '../utils/popups';
import { postItem } from '../utils/api';
import { Section } from "./MainPage";
import { useNavigate } from 'react-router-dom';
interface ListProps {
    section: Section;
    setSection: React.Dispatch<React.SetStateAction<Section>>
}
interface FormData {
    title: string;
    price: string;
    status: string;
    condition: string;
    description: string;
    seller: string;
    images: File[] | null;
    category: string;

}
const ItemForm = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState<FormData>({
        title: '',
        price: '',
        status: 'available',
        condition: '',
        description: '',
        seller: '',
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
        console.log(getLoginId())
        const submitButton = document.getElementById('submitButton');
        submitButton.disabled = true;
        const seller = getUserId();
        const submissionData = new FormData();
        submissionData.append('title', formData.title);
        submissionData.append('price', formData.price);
        submissionData.append('status', formData.status);
        submissionData.append('condition', formData.condition);
        submissionData.append('description', formData.description);
        submissionData.append('category', formData.category);
        if (seller) {
            submissionData.append('seller', seller);
        } else {
            console.error("Seller ID is undefined.");
            return; // Optionally halt the submission or handle appropriately
        }
        if (formData.images) {
            formData.images.forEach((file, index) => {
                submissionData.append(`images[${index}]`, file);
            });
        }else{
            showErrorPopup("Please upload item images.")
        }

        postItem(submissionData).then((data)=>{
            const submitButton = document.getElementById('submitButton');
            submitButton.disabled = false;
            submitButton.textContent = 'Submit';
            messagePopup("Item added successfully, now directing you to the detailed item page.");
            navigate(`/item-details/${data.itemId}`);
        }).catch((error)=>{
            showErrorPopup("Failed to add item."+ error.message);
        })
        // };
    };
    return (
        <div>
        <h1 className="post-title"> 🐻📝 What item do you want to sell? </h1>
        <div className="form-container">

            <form id="post-form"onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <input type="text" name="title" value={formData.title} onChange={handleChange} placeholder="Title" className="form-input" autoComplete="off"/>
                <div className="form-group">
                <label htmlFor="title">Category:</label>
                <select name="category" value={formData.category} onChange={handleChange} className="form-select">
                    <option value="Furniture">furniture</option>
                    <option value="Clothing">clothing</option>
                    <option value="Kitchen">kitchen</option>
                    <option value="Others">others</option>
                </select>
                </div>
                <input type="text" name="price" value={formData.price} onChange={handleChange} placeholder="Price" className="form-input" autoComplete="off"/>
                <input type="text" name="condition" value={formData.condition} onChange={handleChange} placeholder="Condition" className="form-input" autoComplete="off"/>
                <textarea name="description" value={formData.description} onChange={handleChange} placeholder="Description" className="form-textarea"autoComplete="off"></textarea>
                <input type="file" name="image" onChange={handleImageChange} className="form-input" multiple />
                <button type="submit" id = "submitButton" className="form-button">Save</button>
            </form>
        </div>
        </div>
    );
};
export default ItemForm;
