// import "../styles/items.css";
import "../styles/main.css";
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Item } from "../utils/schemas";
import { modifyWatchList, getWatchList, searchItems, getAllItems } from "../utils/api"; // Updated import
import { getUserId } from "../utils/cookie";
import { Section } from "./MainPage";

export interface ListProps {
    section: Section;
    setSection: React.Dispatch<React.SetStateAction<Section>>
}

export default function SearchPage(props: ListProps) {
    const [data, setData] = useState<Item[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [watchList, setWatchList] = useState<string[]>([]);

    useEffect(() => {
        if (searchTerm.trim()) {
            searchItems(searchTerm).then((response) => {
                if (response.status === 200) {
                    setData(response.items);
                } else {
                    console.error('Search failed:', response.error);
                }
            }).catch((error) => {
                console.error('Error fetching search results:', error);
            });
        } else {
            getAllItems().then((response) => {
                if (response.status === 200) {
                    setData(response.items);
                } else {
                    console.error('Failed to fetch items:', response.error);
                }
            }).catch((error) => {
                console.error('Error fetching items:', error);
            });
        }
    }, [searchTerm]); 

    const toggleWatchList = (itemId: string) => {
        const operation = watchList.includes(itemId) ? 'del' : 'add';
        modifyWatchList(getUserId(), itemId, operation).then(() => {
            if (operation === 'add') {
                setWatchList([...watchList, itemId]);
            } else {
                setWatchList(watchList.filter(id => id !== itemId));
            }
        }).catch((error) => {
            console.error('Error modifying watch list:', error);
        });
    };

    return (
        <div className="item-page">
            <div className="search-box">
                <input
                    type="text"
                    placeholder="Search for items..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <button onClick={() => setSearchTerm('')}>Clear</button>
            </div>
            <div className="item-list">
                {data.map((item: Item) => (
                    <div key={item.id} className="item-container">
                        <Link to={`/item-details/${item.id}`} className="link-style" onClick={() => props.setSection(Section.VIEW_ITEM_DETAILS)}>
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
                        <button onClick={() => toggleWatchList(item.id)} className="watchlist-button">
                            {/* SVG icons */}
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
}