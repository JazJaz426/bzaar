import React from 'react';
import { Section } from './MainPage'; // Make sure to export Section from MainPage or better yet, move it to a common types file.
import { Link } from 'react-router-dom';

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
          <li className={currentSection === Section.VIEW_ITEM && !listView ? 'active' : ''}>
            <a href="#" onClick={() => onNavClick(Section.VIEW_ITEM)}>Discover</a>
          </li>
          <li className={currentSection === Section.VIEW_ITEM && listView ? 'active' : ''}>
            <a href="#" onClick={() => onNavClick(Section.VIEW_ITEM, true)}>Watch List</a>
          </li>
          <li className={currentSection === Section.SELLING ? 'active' : ''}>
            <a href="#" onClick={() => onNavClick(Section.SELLING)}>Listings</a>
          </li>
          <li className={currentSection === Section.PROFILE ? 'active' : ''}>
            <a href="#" onClick={() => onNavClick(Section.PROFILE)}>Profile</a>
          </li>
        </ul>
      </nav>
    </div>
  );
};

export default Sidebar;