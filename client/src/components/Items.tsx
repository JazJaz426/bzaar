import "../styles/items.css";
import "../styles/main.css";
import React, { useState, useEffect } from 'react';
import { db } from "./firebase.js";
import { collection, getDocs, query, where } from "firebase/firestore";
import { Link } from 'react-router-dom';

interface ListProps {}

type Item = {
    id: string;
    title: string;
    status: string;
    price: string;
    images: string[];
};

export default function Profile(props: ListProps) {
    const [data, setData] = useState<Item[]>([]);
    const [searchTerm, setSearchTerm] = useState('');

    useEffect(() => {
        const fetchData = async () => {
            const collectionRef = collection(db, 'items');
            const q = query(collectionRef, where("title", ">=", searchTerm), where("title", "<=", searchTerm + '\uf8ff'));
            const querySnapshot = await getDocs(q);
            setData(querySnapshot.docs.map(doc => ({ id: doc.id, ...doc.data() as Item })));
        };

        fetchData();
    }, [searchTerm]);

    return (
        <div className="item-page">
            <div className="search-box">
                <input type="text" placeholder="Search for items..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
                <button onClick={() => setSearchTerm('')}>Search</button>
            </div>
            <div className="item-list">
                {data.map((item: Item) => (
                    <Link to={`/item-details/${item.id}`} key={item.id} className="link-style">
                        <div className="item-box">
                            <div className="item-image-box">
                                <img src={item.images[0]} alt={item.title} />
                            </div>
                            <div className="item-info">
                                <div className="item-info-left">
                                    <p className="item-name">{item.title}</p>
                                    <p className="item-status" style={{ color: item.status === 'available' ? '#4CAF50' : '#FF5722' }}>
                                        {item.status}
                                    </p>
                                </div>
                                <p className="item-price">${item.price}</p>
                            </div>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
}
