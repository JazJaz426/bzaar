import { useState } from "react";
import "../styles/MainPage.css";
import Items from "./Items";
import Profile from "./Profile";
import Sidebar from "./Sidebar";
import Layout from "./Layout"; // Import Layout
import Selling from "./Selling";
import WatchList from "./WatchList";

export enum Section {
    VIEW_ITEM= "VIEW_ITEM",
    SELLING= "SELLING",
    PROFILE= "PROFILE",
    WATCHLIST= "WATCHLIST",
    VIEW_ITEM_DETAILS= "VIEW_ITEM_DETAILS"
}

export default function MainPage() {
  const [section, setSection] = useState<Section>(Section.VIEW_ITEM);
  const [listView, setListView] = useState<boolean>(false);

  const handleNavClick = (section: Section, listView: boolean = false) => {
    console.log("Nav clicked:", section, listView);
    setSection(section);
    setListView(listView);
  };

  return (
    <Layout currentSection={section} onNavClick={handleNavClick}>
      {section === Section.VIEW_ITEM ? <Items section={section} setSection={setSection} /> : null}
      {section === Section.SELLING ? <Selling section={section} setSection={setSection}/> : null}
      {section === Section.WATCHLIST ? <WatchList section={section} setSection={setSection} /> : null}
      {section === Section.PROFILE ? <Profile email_address={""} pick_up_location={""} /> : null}
    </Layout>
  );
}

