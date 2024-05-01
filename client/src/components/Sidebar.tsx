import React from 'react';
import { Section } from './MainPage'; // Make sure to export Section from MainPage or better yet, move it to a common types file.
import { Link } from 'react-router-dom';
import '../styles/Sidebar.css';

interface SidebarProps {
  currentSection: Section;
  listView?: boolean;
  onNavClick: (section: Section, listView?: boolean) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ currentSection, listView, onNavClick }) => {
  return (
    <div className="sidebar">
      <nav className="nav">
        <ul>
          <li className={currentSection === Section.DISCOVER && !listView ? 'active' : ''}>
            <a href="#" onClick={() => onNavClick(Section.DISCOVER)}>Discover</a>
          </li>
          <li className={currentSection === Section.SEARCHPAGE && !listView ? 'active' : ''}>
            <a href="#" onClick={() => onNavClick(Section.SEARCHPAGE)}>Search</a>
          </li>
          <li className={currentSection === Section.WATCHLIST && !listView ? 'active' : ''}>
            <a href="#" onClick={() => onNavClick(Section.WATCHLIST, true)}>Watch List</a>
          </li>
          <li className={currentSection === Section.CLAIMLIST && !listView ? 'active' : ''}>
            <a href="#" onClick={() => onNavClick(Section.CLAIMLIST, true)}>Claim List</a>
          </li>
          <li className={currentSection === Section.SELLING && !listView ? 'active' : ''}>
            <a href="#" onClick={() => onNavClick(Section.SELLING)}>My Listings</a>
          </li>
          <li className={currentSection === Section.PROFILE && !listView ? 'active' : ''}>
            <a href="#" onClick={() => onNavClick(Section.PROFILE)}>Profile</a>
          </li>
          <li>
            <Link to="/post" className="post-button">Post</Link>
          </li>
        </ul>
      </nav>
    </div>
  );
};

export default Sidebar;