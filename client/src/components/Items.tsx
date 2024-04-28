import "../styles/items.css";
import "../styles/main.css"
// to be deleted
import React, { useState, useEffect } from 'react';
import {db} from "./firebase.js";
import { collection, getDocs } from "firebase/firestore";

interface ListProps {
    // 1. a list of items to show

}

type Item = {
    id: string;
    [key: string]: any; // This allows for any other properties that might come from the document
  };
  
/**
 * @func Profile
 * @description 'return the user profile component '
 * @param props: ProfileProps
 */
export default function Profile(props: ListProps) {
const [data, setData] = useState<Item[]>([]);
  console.log(data);
  useEffect(() => {
    // Function to fetch data
    const fetchData = async () => {
      // Create a reference to the collection you want to fetch
      const collectionRef = collection(db, 'items'); // Replace with your collection name
      // Fetch the snapshot
      const querySnapshot = await getDocs(collectionRef);
      // Map through the documents and set data in state
      setData(querySnapshot.docs.map(doc => ({ id: doc.id, ...doc.data() })));
    };
    // Call the function to fetch data
    fetchData();
  }, []);

  return (
    <div className="item-page">
        <div className="search-box">
            <input type="text" placeholder="Search for items..." />
            <button>Search</button>
        </div>
        <div className="item-list">
            {data.map((item: any) => (
                <div className="item-box">
                    <div className="item-image-box">
                    <img src={item.image.get} alt="item" />
                    </div>
                    <div className="item-info">
                        <div className="item-info-left">
                            <p className="item-name">{item.title}</p>
                            <p className="item-status">{item.status}</p>
                        </div>
                        <p className="item-price">{item.price}</p>
                    </div>
                </div>
            ))}
            
           
            {/* <div className="item-box">
                <div className="item-image-box">
                <img src="https://images.pexels.com/photos/100582/pexels-photo-100582.jpeg?cs=srgb&dl=pexels-luftschnitzel-100582.jpg&fm=jpg" alt="item" />
                </div>
                <div className="item-info">
                    <div className="item-info-left">
                    <p className="item-name">Bike</p>
                    <p className="item-status">Available</p>
                    </div>
                    <p className="item-price">$100</p>
                </div>
            </div>
            <div className="item-box">
                <div className="item-image-box">
                <img src="https://images.pexels.com/photos/100582/pexels-photo-100582.jpeg?cs=srgb&dl=pexels-luftschnitzel-100582.jpg&fm=jpg" alt="item" />
                </div>
                <div className="item-info">
                    <p className="item-name">Bike</p>
                    <p className="item-status">Available</p>
                    <p className="item-price">$100</p>
                </div>
            </div>
            <div className="item-box">
                <div className="item-image-box">
                <img src="https://images.pexels.com/photos/100582/pexels-photo-100582.jpeg?cs=srgb&dl=pexels-luftschnitzel-100582.jpg&fm=jpg" alt="item" />
                </div>
                <div className="item-info">
                    <p className="item-name">Bike</p>
                    <p className="item-status">Available</p>
                    <p className="item-price">$100</p>
                </div>
            </div>
            <div className="item-box">
                <div className="item-image-box">
                <img src="https://images.pexels.com/photos/100582/pexels-photo-100582.jpeg?cs=srgb&dl=pexels-luftschnitzel-100582.jpg&fm=jpg" alt="item" />
                </div>
                <div className="item-info">
                    <p className="item-name">Bike</p>
                    <p className="item-status">Available</p>
                    <p className="item-price">$100</p>
                </div>
            </div> */}
        </div>
      </div>
  );
}
