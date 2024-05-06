import { useState } from "react";
import "../styles/MainPage.css";
import SearchPage from "./SearchPage";
import Discover from "./Discover";
import Profile from "./Profile";
import Sidebar from "./Sidebar";
import Layout from "./Layout"; // Import Layout
import Selling from "./Selling";
import WatchList from "./WatchList";
import ClaimList from "./ClaimList";
import ItemForm from "./ItemForm";
import { Section } from "../utils/schemas";
import { SectionHistoryProps } from "../utils/schemas";

export default function MainPage() {
  const [section, setSection] = useState<Section>(Section.DISCOVER);
  const [listView, setListView] = useState<boolean>(false);

  const handleNavClick = (section: Section, listView: boolean = false) => {
    console.log("Nav clicked:", section, listView);
    setSection(section);
    setListView(listView);
  };
  return (
    <Layout currentSection={section} onNavClick={handleNavClick}>
      {section === Section.DISCOVER ? <Discover section={section} setSection={setSection} /> : null}
      {section === Section.SEARCHPAGE ? <SearchPage section={section} setSection={setSection} /> : null}
      {section === Section.SELLING ? <Selling section={section} setSection={setSection}/> : null}
      {section === Section.WATCHLIST ? <WatchList section={section} setSection={setSection} /> : null}
      {section === Section.CLAIMLIST ? <ClaimList section={section} setSection={setSection} /> : null}
      {section === Section.PROFILE ? <Profile email_address={""} pick_up_location={""} /> : null}
      {section === Section.POST ? <ItemForm /> : null}
    </Layout>
  );
}

