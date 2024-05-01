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
    const [searchTerm, setSearchTerm] = useState('');
    const userId = getUserId(); // This should be dynamically set based on the logged-in user

    const fetchData = async () => {
        const allItemsData = await getAllItems();
        const claimListData = await getClaimList(userId);
        // console.log('watchListData', watchListData);
        // console.log('allItemsData', allItemsData);
        setClaimList(claimListData.claimlist);
        setData(allItemsData.items.filter((item: Item) => claimListData.claimlist.includes(item.id)));
    };

    useEffect(() => {
        fetchData();
    }, [searchTerm]);
    return (
        <div className="item-page">
            <div className="item-list">
                {data.map((item: Item) => (
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
                ))}
                {data.length === 0 && (
                    <div className="centered-message">
                        <p>No items in your claim list.</p>
                    </div>
                )}
            </div>
        </div>
    );
}

