import "../styles/items.css";
import "../styles/main.css";
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllItems, getClaimList } from "../utils/api";
import { Item } from "../utils/schemas";
import { getUserId } from "../utils/cookie";
import { ListProps } from "./Items";
import { Section } from "./MainPage";

export default function ClaimList(props: ListProps) {
    const [data, setData] = useState<Item[]>([]);
    const [claimList, setClaimList] = useState<string[]>([]);
    const [isLoading, setIsLoading] = useState(true); 
    const userId = getUserId();

    const fetchData = async () => {
        setIsLoading(true);  
        try {
            const allItemsData = await getAllItems();
            const claimListData = await getClaimList(userId);
            setClaimList(claimListData.claimlist);
            setData(allItemsData.items.filter((item: Item) => claimListData.claimlist.includes(item.id)));
        } finally {
            setIsLoading(false); 
        }
    };

    useEffect(() => {
        fetchData();
    }, []);  

    return (
        <div className="item-page">
            <div className="item-list">
                {isLoading ? (
                    <div className="centered-message">
                        <p>Loading your claim list...</p>
                    </div>
                ) : data.length > 0 ? (
                    data.map((item: Item) => (
                        <Link to={`/item-details/${item.id}`} key={item.id} className="link-style" onClick={() => props.setSection(Section.VIEW_ITEM_DETAILS)}>
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
                ) : (
                    <div className="centered-message">
                        <p>No items in your claim list.</p>
                    </div>
                )}
            </div>
        </div>
    );
}