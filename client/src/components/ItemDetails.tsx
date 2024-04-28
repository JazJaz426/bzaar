import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { db } from './firebase.js';
import { doc, getDoc, getDocs, collection, query, where } from 'firebase/firestore';

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
                const userDoc = querySnapshot.docs[0]; // Assuming the user_id is unique and only one document is returned
                setSeller(userDoc.data() as User);
            } else {
                console.log("No such user!");
            }
        };

        fetchItem();
    }, [id]);

    if (!item) {
        return <div>Loading...</div>;
    }

    return (
        <div>
          <h1>{item.title}</h1>
          <img src={item.images[0]} alt={item.title} />
          <p>Description: {item.description}</p>
          <p>Category: {item.category} </p>
          <p>Price: ${item.price}</p>
          <p>Status: {item.status}</p>
          <p>Seller: {seller ? seller.name : 'Seller name not available'}</p>
          <p>Email: {seller ? seller.email : 'Email not available'}</p>
          <p>Address: {seller ? seller.address : 'Address not available'}</p>
        </div>
      );
};

export default ItemDetail;