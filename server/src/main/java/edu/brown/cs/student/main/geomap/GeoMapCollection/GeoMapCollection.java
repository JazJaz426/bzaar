package edu.brown.cs.student.main.geomap.GeoMapCollection;

import edu.brown.cs.student.main.geomap.GeoMapCollection.GeoMap.GeoMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeoMapCollection {
  public String type;
  public List<GeoMap> features;

  // better to leave some documentation comment
  public GeoMapCollection filterGeoJSONByBounds(
      double minLat, double maxLat, double minLon, double maxLon) {
    GeoMapCollection filteredGeoJSON = new GeoMapCollection();
    filteredGeoJSON.features =
        this.features.stream()
            .filter(
                feature ->
                    feature.geometry != null
                        && feature.geometry.coordinates.stream() // Stream of MultiPolygons
                            .flatMap(List::stream) // Stream of Polygons
                            //                .flatMap(List::stream) // Stream of LinearRings/Points
                            .anyMatch(
                                linearRing ->
                                    linearRing.stream()
                                        .anyMatch(
                                            coord -> {

                                              // Directly access the Double values by index
                                              double lon = coord.get(0);
                                              double lat = coord.get(1);
                                              //
                                              // Check if the coordinate falls within the bounds
                                              return lat >= minLat
                                                  && lat <= maxLat
                                                  && lon >= minLon
                                                  && lon <= maxLon;
                                            })))
            .collect(Collectors.toList());
    return filteredGeoJSON;
  }

  public GeoMapCollection filterGeoJSONByKeyword(String keyword) {
    GeoMapCollection filteredGeoJSON = new GeoMapCollection();
    filteredGeoJSON.features =
        this.features.stream()
            .filter(
                feature ->
                    feature.geometry != null
                        && feature.properties != null // Ensure properties is not null
                        && containsKeyword(
                            feature.properties.area_description_data,
                            keyword)) // Check if the value contains the keyword
            .collect(Collectors.toList());

    return filteredGeoJSON;
  }

  // Helper method to check if any value in area_description_data Map<String, String> contains the
  // keyword
  private boolean containsKeyword(Map<String, String> areaDescriptionData, String keyword) {
    return areaDescriptionData.values().stream().anyMatch(value -> value.contains(keyword));
  }
}
