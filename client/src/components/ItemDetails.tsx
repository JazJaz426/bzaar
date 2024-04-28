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
            if (id === undefined) {
                console.log("Document ID is undefined.");
                return;
            }
            const docRef = doc(db, 'items', id);
            const docSnap = await getDoc(docRef);

            if (docSnap.exists()) {
                const itemData = docSnap.data() as Item;
                setItem(itemData);
                fetchSellerDetails(itemData.seller);
            } else {
                console.log("No such document!");
            }
        };

        const fetchSellerDetails = async (userId: string) => {
            const usersRef = collection(db, "users");
            const q = query(usersRef, where("user_id", "==", userId));
            const querySnapshot = await getDocs(q);
            if (!querySnapshot.empty) {
                const userDoc = querySnapshot.docs[0];
                setSeller(userDoc.data() as User);
            } else {
                console.log("No such user!");
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