import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { db } from './firebase.js';
import { doc, getDoc, getDocs, collection, query, where } from 'firebase/firestore';
import Layout from './Layout';
import { Section } from './MainPage'; 
import ImageCarousel from './ImageCarousel';
import { getItemDetails, getSellerProfile } from '../utils/api';
import '../styles/itemDetails.css'; // Assuming CSS module usage

interface Item {
    id: string;
    title: string;
    description?: string;
    images: string[];
    price: number;
    category: string,
    condition: string,
    status: string;
    seller: string;
}

interface User {
    id: string;
    name: string;
    email: string;
    address: string;
    user_id: string;
}


const ItemDetail = () => {
    const { id } = useParams<{ id: string }>();
    const [item, setItem] = useState<Item | null>(null);
    const [seller, setSeller] = useState<User | null>(null);

    useEffect(() => {
        const fetchItemDetails = async () => {
            if (!id) {
                console.log("Document ID is undefined.");
                return;
            }
            try {
                const itemResponseMap = await getItemDetails(id);
                const itemData = itemResponseMap.data;
                setItem(itemData)
                if (itemData.seller) {
                    fetchSellerDetails(itemData.seller);
                }
            } catch (error) {
                console.error("Error fetching item details:", error);
            }
        };

        const fetchSellerDetails = async (userId: string) => {
            try {
                const sellerResponseMap = await getSellerProfile(userId); // Assuming getUserProfile can accept a userId
                const sellerData = sellerResponseMap.data;
                setSeller(sellerData);
            } catch (error) {
                console.error("Error fetching seller details:", error);
            }
        };

        fetchItemDetails();
    }, [id]);

    if (!item) {
        return <div className="loading">Loading...</div>;
    }

    return (
        <Layout currentSection={Section.VIEW_ITEM}>
            <div className="itemDetailContainer">
                <h1 className="title">{item.title}</h1>
                <ImageCarousel images={item.images} />
                <p className="description">Description: {item.description}</p>
                <p>Category: {item.category}</p>
                <p>Price: ${item.price}</p>
                <p>Status: {item.status}</p>
                <p>Condition: {item.condition}</p>
                <div className="sellerInfo">
                    <p>Seller: {seller ? seller.name : 'Seller name not available'}</p>
                    <p>Email: {seller ? seller.email : 'Email not available'}</p>
                    <p>Address: {seller ? seller.address : 'Address not available'}</p>
                </div>
            </div>
        </Layout>
    );
};

export default ItemDetail;