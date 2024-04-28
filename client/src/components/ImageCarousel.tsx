import React, { useState } from 'react';
import '../styles/ImageCarousel.css';

type ImageCarouselProps = {
    images: string[];
  };

  const ImageCarousel: React.FC<ImageCarouselProps> = ({ images }) => {
    const [currentIndex, setCurrentIndex] = useState(0);

  const goToPrevious = () => {
    setCurrentIndex((prevIndex) =>
      prevIndex > 0 ? prevIndex - 1 : images.length - 1
    );
  };

  const goToNext = () => {
    setCurrentIndex((prevIndex) =>
      prevIndex < images.length - 1 ? prevIndex + 1 : 0
    );
  };

  return (
    <div className="carousel-container">
        {images.length > 1 && (
            <button className="left-arrow" onClick={goToPrevious}>&lt;</button>
        )}
        <img src={images[currentIndex]} alt="Item" className="carousel-image" />
        {images.length > 1 && (
            <button className="right-arrow" onClick={goToNext}>&gt;</button>
        )}
    </div>
);
};

export default ImageCarousel;