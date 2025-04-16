import "../styles/items.css";
import "../styles/main.css";
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getItemsByUser, deleteItem } from "../utils/api";
import { getUserId } from '../utils/cookie';
import { Item } from "../utils/schemas";
import { showErrorPopup, messagePopup } from "../utils/popups";
import { ListProps } from "../utils/schemas";
import { Section } from "../utils/schemas";

export default function Selling(props: ListProps) {
    const [data, setData] = useState<Item[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    const fetchData = () => {
        setIsLoading(true);  
        getItemsByUser(getUserId()).then((data) => {
            setData(data.items.map((doc: Item) => ({ id: doc.id, ...doc })));
            setIsLoading(false); 
        });
    }

    const deleteSellItem = (itemId: string) => {
        deleteItem(itemId, getUserId()).then(() => {
            messagePopup("Item deleted successfully.");
            fetchData();
        }).catch((error) => {
            console.error(error);
            showErrorPopup('Failed to delete item. Please try again later.');
        });
    };

    useEffect(() => {
        fetchData();
    }, []);

    return (
        <div className="item-page">
            <div className="item-list">
                {isLoading ? (
                    <div className="centered-message">
                        <p>Loading...</p>
                    </div>
                ) : data.length === 0 ? (
                    <div className="centered-message">
                        <p>No items to be sold yet. Please add some items to sell.</p>
                    </div>
                ) : (
                    data.map((item: Item) => (
                        <div key={item.id} className="item-container">
                            <Link to={`/item-details/${item.id}`} key={item.id} className="link-style" onClick={() => {props.setSection(Section.VIEW_ITEM_DETAILS); props.setSectionHistory([...props.sectionHistory, Section.VIEW_ITEM_DETAILS])}}>
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
                            <button onClick={() => deleteSellItem(item.id)} id="deleteButton" className="watchlist-button"
                            style={{ background: 'none', border: 'none' }}>
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="red" viewBox="0 0 24 24">
                                    <line x1="6" y1="6" x2="18" y2="18" stroke="#666" strokeWidth="2"/>
                                    <line x1="6" y1="18" x2="18" y2="6" stroke="#666" strokeWidth="2"/>
                                </svg>
                                <span className="tooltip"> Delete or mark as sold </span>
                            </button>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}