import React, { useState } from 'react';
import { searchItems } from '../utils/api';

interface SearchItemsProps {
  onSearchResults: (items: any[]) => void;  // Callback to pass search results to the parent component
}

function SearchItems({ onSearchResults }: SearchItemsProps) {
  const [keyword, setKeyword] = useState('');

  const handleSearch = async () => {
    if (!keyword.trim()) {
      alert('Please enter a keyword to search.');
      return;
    }
    try {
      const data = await searchItems(keyword);
      if (data.status === 200) {
        onSearchResults(data.items);  // Pass the search results to the parent component
      } else {
        alert(data.error); // Display error message from the server
      }
    } catch (error) {
      console.error('Search failed:', error);
      alert('Failed to perform search. Please try again later.');
    }
  };

  return (
    <div>
      <input
        type="text"
        value={keyword}
        onChange={(e) => setKeyword(e.target.value)}
        placeholder="Search for items..."
      />
      <button onClick={handleSearch}>Search</button>
    </div>
  );
}

export default SearchItems;