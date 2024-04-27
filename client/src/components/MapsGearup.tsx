import { useState } from "react";
import FirestoreDemo from "./Retired_components/FirestoreDemo";
import Mapbox from "./Mapbox";
import REPL from "./REPL";

enum Section {
  FIRESTORE_DEMO = "FIRESTORE_DEMO",
  MAP_DEMO = "MAP_DEMO",
}

export default function MapsGearup() {

  return (
    <div className="container">
      {/* NOTE: user story 2 changed to no longer require display of other dataset alongside map
      <div aria-label="Dataset display" className="left-panel">
        <h1>REPL</h1>
        <REPL />
      </div> */}
      <div aria-label="Redlining Map" className="right-panel">
        <h1>Redlining Map</h1>
        <Mapbox />
      </div>
    </div>
    
  );

}
