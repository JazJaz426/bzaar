import React from 'react';
import { Section } from './MainPage'; // Make sure to export Section from MainPage or better yet, move it to a common types file.
import { useNavigate } from 'react-router-dom';
import '../styles/Sidebar.css';

interface SidebarProps {
  currentSection: Section;
  listView?: boolean;
  onNavClick: (section: Section, listView?: boolean) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ currentSection, listView, onNavClick }) => {
  const navigate = useNavigate();
  return (
    <div className="sidebar">
      <nav className="nav">
      <img src="../assets/BZaar.png" alt="Bear Icon" className="bear-icon"/>
        <ul>
          <li className={currentSection === Section.DISCOVER && !listView ? 'active' : ''}>
            <a href="#" onClick={() => {
              onNavClick(Section.DISCOVER);
              navigate('/');
            }}>Discover</a>
          </li>
          <li className={currentSection === Section.SEARCHPAGE && !listView ? 'active' : ''}>
            <a href="#" onClick={() => {
              onNavClick(Section.SEARCHPAGE);
              navigate('/');
            }}>Search</a>
          </li>
          <li className={currentSection === Section.WATCHLIST && !listView ? 'active' : ''}>
            <a href="#" onClick={() => {
              onNavClick(Section.WATCHLIST, true);
              navigate('/');
            }}>Watch List</a>
          </li>
          <li className={currentSection === Section.CLAIMLIST && !listView ? 'active' : ''}>
            <a href="#" onClick={() => {
              onNavClick(Section.CLAIMLIST, true);
              navigate('/');
            }}>Claim List</a>
          </li>
          <li className={currentSection === Section.SELLING && !listView ? 'active' : ''}>
            <a href="#" onClick={() => {
              onNavClick(Section.SELLING);
              navigate('/');
            }}>My Listings</a>
          </li>
          <li className={currentSection === Section.PROFILE && !listView ? 'active' : ''}>
            <a href="#" onClick={() => {
              onNavClick(Section.PROFILE);
              navigate('/');
            }}>Profile</a>
          </li>
          <li className={currentSection === Section.POST && !listView ? 'active' : ''}>
            {/*<Link to="/post" className="post-button">Post</Link>*/}
            <a href="#"  id="post-button" onClick={() => onNavClick(Section.POST)}>POST</a>
          </li>
        </ul>
      </nav>
    </div>
  );
};

export default Sidebar;
