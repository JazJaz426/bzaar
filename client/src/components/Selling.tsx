import "../styles/items.css";
import "../styles/main.css";
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getItemsByUser } from "../utils/api";
import { Item } from "../utils/schemas";
import "../styles/items.css";
import "../styles/main.css";

export default function Selling() {
    const [data, setData] = useState<Item[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const fetchData =  () => {
        getItemsByUser().then((data) => {
            console.log('data is');
            console.log(data.items);
            setData(data.items.map((doc: Item) => ({ id: doc.id, ...doc })));
        });
    }

    useEffect(() => {
        fetchData();
    }, [searchTerm]);

    return (
        <div className="item-page">
            <div className="item-list">
                {data.length === 0 ? (
                    <div className="centered-message">
                        <p>No items to be sold yet. Please add some items to sell.</p>
                    </div>
                ) : (
                    data.map((item: Item) => (
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
                    ))
                )}
            </div>
        </div>
    );
}

