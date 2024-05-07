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
import { Section, ListProps } from "../utils/schemas";

export default function MainPage(props: ListProps) {

  return (
    <Layout currentSection={props.section} onNavClick={(newSection) => {
      props.setSection(newSection);
      props.setSectionHistory([...props.sectionHistory, newSection]);
    }}>
      {props.section === Section.DISCOVER ? <Discover section={props.section} setSection={props.setSection} sectionHistory={props.sectionHistory} setSectionHistory={props.setSectionHistory}/> : null}
      {props.section === Section.SEARCHPAGE ? <SearchPage section={props.section} setSection={props.setSection} sectionHistory={props.sectionHistory} setSectionHistory={props.setSectionHistory}/> : null}
      {props.section === Section.SELLING ? <Selling section={props.section} setSection={props.setSection} sectionHistory={props.sectionHistory} setSectionHistory={props.setSectionHistory}/> : null}
      {props.section === Section.WATCHLIST ? <WatchList section={props.section} setSection={props.setSection} sectionHistory={props.sectionHistory} setSectionHistory={props.setSectionHistory}/> : null}
      {props.section === Section.CLAIMLIST ? <ClaimList section={props.section} setSection={props.setSection} sectionHistory={props.sectionHistory} setSectionHistory={props.setSectionHistory}/> : null}
      {props.section === Section.PROFILE ? <Profile email_address={""} pick_up_location={""} /> : null}
      {props.section === Section.POST ? <ItemForm section={props.section} setSection={props.setSection} sectionHistory={props.sectionHistory} setSectionHistory={props.setSectionHistory}/> : null}
    </Layout>
  );
}

