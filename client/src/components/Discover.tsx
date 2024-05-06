import "../styles/items.css";
import "../styles/main.css";
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Item } from "../utils/schemas";
import {modifyWatchList, getWatchList, getAllItems, getRecList, recordUserActivity} from "../utils/api";
import { getUserId } from "../utils/cookie";
import { Section } from "../utils/schemas";
import { ListProps } from "../utils/schemas";



export default function Discover(props: ListProps) {
    const [data, setData] = useState<Item[]>([]);
    const [watchList, setWatchList] = useState<string[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const userId = getUserId(); // This should be dynamically set based on the logged-in user
    console.log('userId', userId);
    const fetchData = async () => {
        setIsLoading(true);
        try {
            const allItemsData = await getAllItems();
            const watchListData = await getWatchList(userId);
            const RecListData = await getRecList(userId);
            setWatchList(watchListData.watchList);
            setData(allItemsData.items.filter((item: Item) => RecListData.reclist.includes(item.id)));
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (userId) {
            fetchData();
        }
    }, [userId]);

    const toggleWatchList = (itemId: string) => {
        if (watchList.includes(itemId)) {
            setWatchList(watchList.filter(id => id !== itemId));
            modifyWatchList(getUserId(), itemId, 'del').then(() => {
                console.log(`Remove item ${itemId} from watch list`); // Placeholder for actual implementation
            }).catch((error) => {
                console.error(error);
            });
        } else {
            setWatchList([...watchList, itemId]);
            modifyWatchList(getUserId(), itemId, 'add').then(() => {
                console.log(`Add item ${itemId} to watch list`); // Placeholder for actual implementation
                recordUserActivity('liked', itemId, getUserId()).then(() => {
                    console.log(`Logged interaction: added item ${itemId} to watch list. interaction type: liked`);
                }).catch((error) => {
                    console.error('Failed to log interaction:', error);
                });
            }).catch((error) => {
                console.error(error);
            });
        }
    };

    return (
        <div className="item-page">
            <div className="item-list">
                {isLoading ? (
                    <div className="centered-message">
                        <p>Loading items...</p>
                    </div>
                ) : data.length > 0 ? (
                    data.map((item: Item) => (
                        <div key={item.id} className="item-container">
                            <Link to={`/item-details/${item.id}`} className="link-style" onClick={() => {props.setSection(Section.VIEW_ITEM_DETAILS);
                                recordUserActivity('clicked', item.id, getUserId()).then(() => {
                                    console.log(`Logged interaction: clicked item ${item.id}. interaction type: clicked`);
                                }).catch((error) => {
                                    console.error('Failed to log interaction:', error);
                                });
                            }}>
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
                            <button onClick={() => toggleWatchList(item.id)} className="watchlist-button" style={{ background: 'none', border: 'none' }}>
                            {watchList.includes(item.id) ?
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="red" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
                                    :
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="grey" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
                                }
                            </button>
                        </div>
                    ))
                ) : (
                    <div className="centered-message">
                        <p>No items to recommend as they are already in your watch list or claimed by you.</p>
                    </div>
                )}
            </div>
        </div>
    );
}
