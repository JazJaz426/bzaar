import { FeatureCollection, GeoJsonProperties, Geometry } from "geojson";
import { FillLayer } from "react-map-gl";

const propertyName = "holc_grade";
export const geoLayer: FillLayer = {
  id: "geo_data",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04",
      "B",
      "#04b8cc",
      "C",
      "#e9ed0e",
      "D",
      "#d11d1d",
      "#ccc",
    ],
    "fill-opacity": 0.3,
  },
};

export const geoLayerGray: FillLayer = {
  id: "base_overlay",
  type: "fill",
  paint: {
    "fill-color": "gray",
    "fill-opacity": 0.01,
  },
};

// TODO: MAPS PART 4:
// - Download and import the geojson file
// - Implement the two functions below.

// Import the raw JSON file
import rl_data from "../geodata/fullDownload.json";
// you may need to rename the downloaded .geojson to .json

function isFeatureCollection(json: any): json is FeatureCollection {
  // ...
  return json.type === "FeatureCollection";
}

export function overlayData(): GeoJSON.FeatureCollection | undefined {
  // ....
  return (isFeatureCollection(rl_data)) ? rl_data : undefined;
}

/**
 * Ensures overlay is correct format before trying to overlay it.
 * @param data subset of GeoJSON dataset, filtered by parameter
 * @returns data or undefined if data is not a GoeJSON.FeatureCollection
 */
export function filteredOverlayData(data: FeatureCollection<Geometry, GeoJsonProperties> | undefined): GeoJSON.FeatureCollection | undefined {
  return (isFeatureCollection(data)) ? data : undefined
}
