import { useState } from "react";
import "../styles/MainPage.css";
enum Section {
    VIEW_ITEM= "VIEW_ITEM",
    SELLING= "SELLING",
    PROFILE= "PROFILE",
}

export default function MapsGearup() {
const [section, setSection] = useState<Section>(Section.VIEW_ITEM);
const [listView, setListView] =useState<boolean>(false);
  return (
    <div>
   
    <div aria-label="Navigation Bar" className="navigation">
    <p aria-label="Gearup Title" id='web-title'>BearZarr</p>
      <div onClick={() => setSection(Section.VIEW_ITEM)}>
      Discover
      </div>
      <div onClick={() => {
        setSection(Section.VIEW_ITEM);
        setListView(true);
      }}>
        Watch List
      </div>
      <div onClick={() => setSection(Section.SELLING)}>
      My Selling
      </div>
      <div onClick={() => setSection(Section.PROFILE)}>
        My Profile
      </div>
    </div>
    {/* {section === Section.DISCOVER ? <Discover /> : null}
    {section === Section.SELLING ? <Selling /> : null}
    {section === Section.PROFILE ? <Profile /> : null} */}
  </div>
    
  );

}
