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
                            {watchList.includes(item.id) ? 
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="red" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg> 
                                : 
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="grey" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
                            }
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
}