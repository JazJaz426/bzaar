import "mapbox-gl/dist/mapbox-gl.css";
import { useEffect, useState } from "react";
import Map, {
  Layer,
  MapLayerMouseEvent,
  Source,
  ViewStateChangeEvent,
  Marker,
} from "react-map-gl";
import { geoLayer, overlayData, filteredOverlayData, geoLayerGray } from "../utils/overlay";
import { addPin, removePin, getPins, filtOver } from "../utils/api";

const MAPBOX_API_KEY = process.env.MAPBOX_TOKEN;
if (!MAPBOX_API_KEY) {
  console.error("Mapbox API key not found. Please add it to your .env file.");
}

export interface LatLong {
  lat: number;
  long: number;
}

// TODO: MAPS PART 1:
// - fill out starting map state and add to viewState
//
const ProvidenceLatLong: LatLong = {
  lat: 41.825226,
  long: -71.418884,
};
const initialZoom = 10;

/**
 * Core function responsible for handling the mapbox, overlays, and pins
 * @returns HTML mapbox element
 */
export default function Mapbox() {
  const [viewState, setViewState] = useState({
    longitude: ProvidenceLatLong.long,
    latitude: ProvidenceLatLong.lat,
    zoom: initialZoom,
  });

  // Set the markers with the mouse's coordinates
  // markers is a map of index to marker objects (coordinate pair)
  const [markers, setMarkers] = useState<LatLong[]>([]);

  /**
   * Load pins from user's firestore db and display when webpage loads
   */
  useEffect(() => {
    async function fetchPins() {
      try {
        const pinsResponse = await getPins();
        console.log(pinsResponse);
        const pins = pinsResponse.pins;

        // Convert list of lists of strings to array of LatLong objects
        const latLongs = pins.map((pin: string[]) => ({
          lat: parseFloat(pin[0]),
          long: parseFloat(pin[1]),
        }));

      // Update markers state with fetched LatLong objects
      setMarkers(latLongs);
      } catch (error) {
        console.error("Error fetching pins: ", error);
      }
    }
    fetchPins();
  }, []);


  // Flag to prevent new marker from being placed if a marker is also being removed
  const [remMarker, setRemMarker] = useState(false);

  /**
   * Place a new pin on the map unless an existing pin is being removed
   * @param event interaction with map layer (click)
   * @returns nothing, used for early escape
   */
  const handleMapClick = async (event: MapLayerMouseEvent) => {
    const newPin = {
      lat: event.lngLat.lat,
      long: event.lngLat.lng,
    };

    try {
      // checks that handleMarkerClick isn't also firing
      if (!remMarker) {
        setMarkers((prevMarkers) => [...prevMarkers, newPin]);
        await addPin(newPin);
      } else {
        return;
      }
      
    } catch (error) {
      console.error("Error adding pin:", error);
    }
  };

  /**
   * Remove the clicked marker from map and user's db
   * @param index index key of marker that was clicked
   */
  const handleMarkerClick = async (index: number) => {
    // Toggle flag (similar to mutex)
    setRemMarker(true);
    const clickedPin = markers[index];
    try {
      setMarkers((prevMarkers) =>
        prevMarkers.filter((_, idx) => idx !== index)
      );
      await removePin(clickedPin);
      
    } catch (error) {
      console.error("Error removing pin:", error);
    } finally {
      // Reset flag
      setRemMarker(false);
    }
  };


  // TODO: MAPS PART 5:
  // - add the overlay useState
  // - implement the useEffect to fetch the overlay data
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined);
  useEffect(() => {
    setOverlay(overlayData());
  }, []);

  const [grayOverlay, setBaseOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined);
  useEffect(() => {
    setBaseOverlay(overlayData());
  }, []);

  
  /**
   * Filters map overlay to only display regions that have a feature matching a given parameter
   * @param query A parameter to filter the overlay on
   */
  const filterOverlay = async (query: string) => {
    try {
      // Checks if query was provided, allows resetting map by giving no param
      if (query.trim() !== "") {
        const newOverlayResp = await filtOver(query);
        // console.log(newOverlayResp);
        setOverlay({
          type: "FeatureCollection",
          features: newOverlayResp.geoResult,
        });
        //setBaseOverlay(overlayData())
      } else {
        setOverlay(overlayData())
      }
    } catch (error) {
      console.error("Error filtering overlay:", error);
    }
  };

  return (
    <div>
      <div aria-label="Overlay filter">
        <label htmlFor="overlay-filter">Add a parameter to filter the overlay:</label>
        <input aria-label="overlay-filter-field" id="overlay-filter" type="text" />
        <button
          aria-label="filter-button"
          onClick={() => {
            const query = (
              document.getElementById("overlay-filter") as HTMLInputElement
            ).value;
            filterOverlay(query);
          }}
        >
          Filter Parameter
        </button>

      </div>
      <div className="map" aria-label="Map display">
        <Map
          mapboxAccessToken={MAPBOX_API_KEY}
          {...viewState}
          // TODO: MAPS PART 2:
          // - add the primary props to the Map (style, mapStyle, onMove).

          style={{width: window.innerWidth, height: window.innerHeight}}
          mapStyle={"mapbox://styles/mapbox/streets-v12"}
          onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)}

          // TODO: MAPS PART 3:
          // - add the onClick handler

          onClick={(ev: MapLayerMouseEvent) => handleMapClick(ev)}
        >
          {/* Render markers 
          TODO: make pins more visible
          */}
        {markers.map((marker, index) => (
          <Marker 
            key={index} 
            latitude={marker.lat} 
            longitude={marker.long}
            anchor="bottom"
            onClick={() => handleMarkerClick(index)}
            aria-label={`Pin ${index}`}
          >
          </Marker>
        ))}
          
        {/* TODO: MAPS PART 6:
            - add the Source and Layer components to the Map that take in data "overlay"
            TODO: MAPS PART 7:
            - add the geoLayer to the Layer component
        */}
          <Source id="geo_data" type="geojson" data={overlay}>
            <Layer {...geoLayer} />
          </Source>
          <Source id="base_overlay" type="geojson" data={grayOverlay}>
            {console.log(grayOverlay)}
            <Layer {...geoLayerGray} paint={{ 'fill-color': 'gray',
                                          'fill-opacity': 0.25
                                        }}/>
          </Source>
          
          
        </Map>
      </div>
    </div>
    
  );
}
