import React from 'react';
import Sidebar from './Sidebar'; // Ensure Sidebar is properly exported from its file
import { Section } from './MainPage';

interface LayoutProps {
    children: React.ReactNode;
    currentSection: Section;
    onNavClick: (section: Section, listView?: boolean) => void;
}

const Layout: React.FC<LayoutProps> = ({ children, currentSection, onNavClick }) => {
    return (
        <div className="app-container">
            <Sidebar currentSection={currentSection} onNavClick={onNavClick} />
            <div className="content">
                {children}
            </div>
        </div>
    );
};

export default Layout;