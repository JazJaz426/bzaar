import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { db } from './firebase.js';
import { doc, getDoc, getDocs, collection, query, where } from 'firebase/firestore';
import Layout from './Layout';
import { Section } from './MainPage'; 
import ImageCarousel from './ImageCarousel';
import '../styles/itemDetails.css'; // Assuming CSS module usage

interface Item {
    id: string;
    title: string;
    description?: string;
    images: string[];
    price: number;
    category: string,
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
        const fetchItem = async () => {
            if (!id) {
                console.log("Document ID is undefined.");
                return;
            }
            try {
                const response = await fetch(`http://localhost:3232/getItemDetails?id=${id}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });
                if (!response.ok) {
                    throw new Error(`Failed to fetch: ${response.status} ${response.statusText}`);
                }
                const data = await response.json();
                setItem(data);
                fetchSellerDetails(data.seller);
                console.log(data.seller)
            } catch (error) {
                console.error("Error fetching item details:", error);
            }
        };

       
        const fetchSellerDetails = async (userId: string) => {
            try {
                const response = await fetch(`http://localhost:3232/getUserProfile?userId=${userId}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });
                if (!response.ok) {
                    throw new Error(`Failed to fetch: ${response.status} ${response.statusText}`);
                }
                const data = await response.json();
                setSeller(data);
            } catch (error) {
                console.error("Error fetching seller details:", error);
            }
        };

        fetchItem();
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