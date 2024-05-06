import React, { useEffect, useState } from 'react';
import { useParams,useNavigate } from 'react-router-dom';
import { db } from './firebase.js';
import { doc, getDoc, getDocs, collection, query, where } from 'firebase/firestore';
import Layout from './Layout';
import { Section } from '../utils/schemas';
import ImageCarousel from './ImageCarousel';
import { getItemDetails, getSellerProfile, claimItem, recordUserActivity, getClaimList, modifyClaimList } from '../utils/api';
import '../styles/itemDetails.css'; // Assuming CSS module usage
import { getUserId } from '../utils/cookie.js';
import Items from './Items';
import Selling from './Selling';
import WatchList from './WatchList';
import Profile from './Profile';
import ClaimList from './ClaimList.js';
import Discover from './Discover.js';
import SearchPage from './SearchPage.js';
import { ListProps } from '../utils/schemas';

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

const ItemDetail = (props: ListProps) => {
    const { id } = useParams<{ id: string }>();
    const [item, setItem] = useState<Item | null>(null);
    const [seller, setSeller] = useState<User | null>(null);
    // const [isClaimedByUser, setIsClaimedByUser] = useState(false);
    const [userClaimList, setUserClaimList] = useState<string[]>([]);
    // const [section, setSection] = useState<Section>(Section.VIEW_ITEM_DETAILS);
    // const [listView, setListView] = useState<boolean>(false);
    // const handleNavClick = (section: Section, listView: boolean = false) => {
    //     console.log("Nav clicked:", section, listView);
    //     setSection(section);
    //     setListView(listView);
    // };
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
    const navigate = useNavigate();
    const handleReturn = () => {
        navigate(-1); // This replaces history.goBack()
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
                recordUserActivity('claimed', id, getUserId()).then(() => {
                    console.log(`Logged interaction: added item ${id} to watch list. interaction type: claimed`);
                }).catch((error) => {
                    console.error('Failed to log interaction:', error);
                });
                }).catch((error) => {
                    console.error(error);
                });
            }

            // Perform the claim operation
            try {
                await claimItem(id);
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

    console.log("current section in item details is: ", props.section)

    if (!item) {
        return <div className="loading">Loading...</div>;
    }
    return (
        <Layout currentSection={props.section} onNavClick={props.setSection}>
            

            {props.section === Section.VIEW_ITEM_DETAILS && (
                <div>
                    <button onClick={handleReturn} className="returnButton">Return</button>
                <div className="itemDetailContainer">
                    <h1 className="title">{item.title}</h1>
                    <ImageCarousel images={item.images} />
                    <p className="description">Description: {item.description}</p >
                    <p className="category">Category: {item.category}</p >
                    <p className="price">Price: ${item.price}</p >
                    <p className="status">Status: {item.status}</p >
                    <p>Condition: {item.condition}</p >
                    <div className="sellerInfo">
                        <p>Seller: {seller ? seller.name : 'Seller name not available'}</p >
                        <p>Email: {seller ? seller.email : 'Email not available'}</p >
                        <p>Address: {seller ? seller.address : 'Address not available'}</p >
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
                    </div>
            )}
            {props.section === Section.DISCOVER ? <Discover section={props.section} setSection={props.setSection} /> : null}
            {props.section === Section.SEARCHPAGE ? <SearchPage section={props.section} setSection={props.setSection} /> : null}
            {props.section === Section.SELLING ? <Selling section={props.section} setSection={props.setSection} /> : null}
            {props.section === Section.WATCHLIST ? <WatchList section={props.section} setSection={props.setSection} /> : null}
            {props.section === Section.CLAIMLIST ? <ClaimList section={props.section} setSection={props.setSection} /> : null}
            {props.section === Section.PROFILE ? <Profile email_address={""} pick_up_location={""} /> : null}
        </Layout>
    );
};

export default ItemDetail;
