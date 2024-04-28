import { useState } from "react";
import "../styles/MainPage.css";
import Items from "./Items";
import Profile from "./Profile";
// import Selling from "./Selling";
enum Section {
    VIEW_ITEM= "VIEW_ITEM",
    SELLING= "SELLING",
    PROFILE= "PROFILE",
}

export default function MapsGearup() {
const [section, setSection] = useState<Section>(Section.VIEW_ITEM);
const [listView, setListView] =useState<boolean>(false);
const handleNavClick = (section: Section, listView: boolean = false) => {
  setSection(section);

  setListView(listView);
  
};
  return (
    <div className="mainpage">
    <div className="sidebar">
    <nav className="nav">
        <ul>
          <li className={section === Section.VIEW_ITEM && !listView ? 'active' : ''}>
            <a href="#" onClick={() => handleNavClick(Section.VIEW_ITEM)}>Discover</a>
          </li>
          <li className={section === Section.VIEW_ITEM && listView ? 'active' : ''}>
            <a href="#" onClick={() => handleNavClick(Section.VIEW_ITEM, true)}>Watch List</a>
          </li>
          <li className={section === Section.SELLING ? 'active' : ''}>
            <a href="#" onClick={() => handleNavClick(Section.SELLING)}>My Selling</a>
          </li>
          <li className={section === Section.PROFILE ? 'active' : ''}>
            <a href="#" onClick={() => handleNavClick(Section.PROFILE)}>My Profile</a>
          </li>
        </ul>
      </nav>
    </div>
    <div className="content">
    {section === Section. VIEW_ITEM ? <Items /> : null}
    {/* {section === Section.SELLING ? <Selling /> : null} */}
    {section === Section.PROFILE ? <Profile email_address={""} pick_up_location={""} /> : null}
    </div>
  </div>
    
  );

}
