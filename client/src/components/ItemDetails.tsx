import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { db } from './firebase.js';
import { doc, getDoc, getDocs, collection, query, where } from 'firebase/firestore';
import Layout from './Layout';
import { Section } from './MainPage'; 
import ImageCarousel from './ImageCarousel';
import { getItemDetails, getSellerProfile, claimItem, logInteraction } from '../utils/api';
import '../styles/itemDetails.css'; // Assuming CSS module usage
import { getUserId } from '../utils/cookie.js';
import Items from './Items';
import Selling from './Selling';
import WatchList from './WatchList';
import Profile from './Profile';


interface Item {
    id: string;
    title: string;
    description?: string;
    images: string[];
    price: number;
    category: string,
    condition: string,
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
    const [isClaimedByUser, setIsClaimedByUser] = useState(false);
    const [section, setSection] = useState<Section>(Section.VIEW_ITEM_DETAILS);
    const [listView, setListView] = useState<boolean>(false);

    useEffect(() => {
        const fetchItemDetails = async () => {
            if (!id) {
                console.log("Document ID is undefined.");
                return;
            }
            try {
                const itemResponseMap = await getItemDetails(id);
                const itemData = itemResponseMap.data;
                setItem(itemData);
                if (itemData.seller) {
                    fetchSellerDetails(itemData.seller);
                }
            } catch (error) {
                console.error("Error fetching item details:", error);
            }
        };

        const fetchSellerDetails = async (userId: string) => {
            try {
                const sellerResponseMap = await getSellerProfile(userId);
                const sellerData = sellerResponseMap.data;
                setSeller(sellerData);
            } catch (error) {
                console.error("Error fetching seller details:", error);
            }
        };

        fetchItemDetails();
    }, [id]);

    const handleClaimItem = async () => {
        if (item && id) {
            const newStatus = item.status === 'claimed' ? 'available' : 'claimed';
            try {
                const responseMap = await claimItem(id);
                const responseStatus = responseMap.status;
                if (responseStatus === 200) {
                    setStatus(newStatus);
                    setIsClaimedByUser(newStatus === 'claimed');
                    try {
                        const logResponse = await logInteraction(getUserId(), id, "claimed");
                        if (logResponse.status === 200) {
                            console.log('Item claimed and interaction logged successfully.');
                        } else {
                            // Handle non-200 status for logging interaction
                            console.log('Item claimed, but failed to log interaction: ' + logResponse.message);
                        }
                    } catch (logError) {
                        console.error('Error logging the interaction:', logError);
                        console.log('Item claimed, but an error occurred while logging the interaction.');
                    }
                } else {
                    alert('Failed to update item status.');
                }
            } catch (error) {
                console.error('Error claiming the item:', error);
                alert('Failed to claim the item.');
            }
        }
    };
    
    const setStatus = (newStatus: string) => {
        console.log('Setting status to:', newStatus); // Debug log
        setItem(prevItem => {
            if (prevItem === null) return null;
            return {
                ...prevItem,
                status: newStatus
            };
        });
    };

    const handleNavClick = (section: Section, listView: boolean = false) => {
        console.log("Nav clicked:", section, listView);
        setSection(section);
        setListView(listView);
    };

    if (!item) {
        return <div className="loading">Loading...</div>;
    }
    console.log("section:", section);     


    return (
        <Layout currentSection={section} onNavClick={handleNavClick}>
            {section === Section.VIEW_ITEM_DETAILS && (
                <div className="itemDetailContainer">
                    <h1 className="title">{item.title}</h1>
                    <ImageCarousel images={item.images} />
                    <p className="description">Description: {item.description}</p>
                    <p>Category: {item.category}</p>
                    <p>Price: ${item.price}</p>
                    <p>Status: {item.status}</p>
                    <p>Condition: {item.condition}</p>
                    <div className="sellerInfo">
                        <p>Seller: {seller ? seller.name : 'Seller name not available'}</p>
                        <p>Email: {seller ? seller.email : 'Email not available'}</p>
                        <p>Address: {seller ? seller.address : 'Address not available'}</p>
                    </div>
                    <button onClick={handleClaimItem} className={`claimButton ${isClaimedByUser ? 'unclaimButton' : ''}`}>
                        {isClaimedByUser ? 'Unclaim' : 'Claim'}
                    </button>
                </div>
            )}
        {section === Section.VIEW_ITEM ? <Items /> : null}
        {section === Section.SELLING ? <Selling /> : null}
        {section === Section.WATCHLIST ? <WatchList /> : null}
        {section === Section.PROFILE ? <Profile email_address={""} pick_up_location={""} /> : null}
        </Layout>
    );
};

export default ItemDetail