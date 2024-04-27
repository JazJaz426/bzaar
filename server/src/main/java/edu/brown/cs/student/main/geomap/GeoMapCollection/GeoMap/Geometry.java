package edu.brown.cs.student.main.geomap.GeoMapCollection.GeoMap;

import java.util.List;

public class Geometry extends GeoMap {
  //  public String type;
  // NOTE: "type" does not work here because we used type already so I did not include a field
  public List<List<List<List<Double>>>> coordinates;

  public List<List<List<List<Double>>>> getCoordinates() {
    return this.coordinates;
  }
}
