import "../styles/items.css";
import "../styles/main.css";
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Item } from "../utils/schemas";
import {modifyWatchList, getWatchList, getAllItems} from "../utils/api";
import { getUserId } from "../utils/cookie";

interface ListProps {}


export default function Items(props: ListProps) {
    const [data, setData] = useState<Item[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [watchList, setWatchList] = useState<string[]>([]);

    const fetchData = () => {
        getAllItems().then((data) => {
            setData(data.items.map((doc: Item) => ({ id: doc.id, ...doc })));
        });
        getWatchList(getUserId()).then((data) => {
            console.log('watchlist is', data.watchList);
            setWatchList(data.watchList);
        });
    }

    useEffect(() => {
        fetchData();
    }, [searchTerm]);

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
            }).catch((error) => {
                console.error(error);
            });
        }
    };

    return (
        <div className="item-page">
            <div className="search-box">
                <input type="text" placeholder="Search for items..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
                <button onClick={() => setSearchTerm('')}>Search</button>
            </div>
            <div className="item-list">
                {data.map((item: Item) => (
                    <div key={item.id} className="item-container">
                        <Link to={`/item-details/${item.id}`} className="link-style">
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
                            {watchList.includes(item.id) ? 'Remove from WatchList' : 'Add to WatchList'}
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
}
