import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { db } from './firebase.js';
import { doc, getDoc, getDocs, collection, query, where } from 'firebase/firestore';
import Layout from './Layout';
import { Section } from './MainPage'; 
import ImageCarousel from './ImageCarousel';
import { getItemDetails, getSellerProfile, claimItem, logInteraction, getClaimList, modifyClaimList } from '../utils/api';
import '../styles/itemDetails.css'; // Assuming CSS module usage
import { getUserId } from '../utils/cookie.js';
import Items from './Items';
import Selling from './Selling';
import WatchList from './WatchList';
import Profile from './Profile';
import ClaimList from './ClaimList.js';
import Discover from './Discover.js';
import SearchPage from './SearchPage.js';
import { getItemDetails, getSellerProfile } from '../utils/api';
import '../styles/itemDetails.css'; // Assuming CSS module usage

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
    // const [isClaimedByUser, setIsClaimedByUser] = useState(false);
    const [userClaimList, setUserClaimList] = useState<string[]>([]);
    const [section, setSection] = useState<Section>(Section.VIEW_ITEM_DETAILS);
    const [listView, setListView] = useState<boolean>(false);
    const userId = getUserId();

    const fetchUserClaimList = async () => {
        const claimListData = await getClaimList(userId);
        setUserClaimList([...claimListData.claimlist]);
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

    const fetchItemDetails = async () => {
        if (!id) {
            console.log("Document ID is undefined.");
            return;
        }
        try {
            const itemResponseMap = await getItemDetails(id);
            const itemData = itemResponseMap.data;
            setItem(itemData);
            // console.log('userClaimList: ', userClaimList, id)
            // setIsClaimedByUser(userClaimList.includes(id));
            if (itemData.seller) {
                fetchSellerDetails(itemData.seller);
            }
        } catch (error) {
            console.error("Error fetching item details:", error);
        }
    };

    useEffect(() => {
        fetchUserClaimList().then(() => {
            fetchSellerDetails(userId).then(() => {
                fetchItemDetails();
            });
        });

    }, [id, userId]);
    

    const handleClaimItem = async () => {
        if (item && id) {
            const operation = userClaimList.includes(id) ? 'del' : 'add';
            if (userClaimList.includes(id)) {
                console.log("user claim list contains current item ", userClaimList.includes(id))
                setUserClaimList(userClaimList.filter(itemId => itemId !== id));
                modifyClaimList(getUserId(), id, 'del').then(() => {
                    console.log(`Remove item ${id} from claim list`); // Placeholder for actual implementation
                }).catch((error) => {
                    console.error(error);
                });
            } else {
                setUserClaimList([...userClaimList, id]);
                modifyClaimList(getUserId(), id, 'add').then(() => {
                    console.log(`Add item ${id} to claim list`); // Placeholder for actual implementation
                }).catch((error) => {
                    console.error(error);
                });
            }
    
            // Perform the claim operation
            try {
                await claimItem(id);
                await logInteraction(userId, id, operation);
                const newStatus = userClaimList.includes(id) ? 'available' : 'claimed';
                setStatus(newStatus);
                // setIsClaimedByUser(!isClaimedByUser);
            } catch (error) {
                console.error('Error updating claim status:', error);
                alert('Failed to update item status.');
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
                        {item.status === 'available' ? (
                            <button onClick={handleClaimItem} className="claimButton">
                                Claim
                            </button>
                        ) : userClaimList.includes(id) ? (
                            <button onClick={handleClaimItem} className="unclaimButton">
                                Unclaim
                            </button>
                        ) : null}
                    </div>
            )}
            {section === Section.DISCOVER ? <Discover /> : null}
            {section === Section.SEARCHPAGE ? <SearchPage /> : null}
            {section === Section.SELLING ? <Selling /> : null}
            {section === Section.WATCHLIST ? <WatchList /> : null}
            {section === Section.CLAIMLIST ? <ClaimList /> : null}
            {section === Section.PROFILE ? <Profile email_address={""} pick_up_location={""} /> : null}
        </Layout>
    );
}; 

export default ItemDetail;