package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.caches.CachedGeo;
import edu.brown.cs.student.main.caches.GeoQuery;
import edu.brown.cs.student.main.geomap.GeoMapCollection.GeoMapCollection;
import edu.brown.cs.student.main.geomap.GeoMapCollection.JSONParser;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class GeoJSONHandler implements Route {
  private CachedGeo cachedGeo;

  public GeoJSONHandler(CachedGeo cachedGeo) {
    try {
      this.cachedGeo = cachedGeo;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Handles the HTTP request to retrieve geojson data. */
  @Override
  public Object handle(Request request, Response response) throws FileNotFoundException {
    Map<String, Object> responseMap = new HashMap<>();

    try {
      String minLat = request.queryParams("minLat");
      String maxLat = request.queryParams("maxLat");
      String minLon = request.queryParams("minLon");
      String maxLon = request.queryParams("maxLon");
      System.out.println(minLat + maxLat + minLon + maxLon);
      if (minLat == null && maxLat == null && minLon == null && maxLon == null) {
        JSONParser myDataSource = new JSONParser("./data/geojson/fulldownload.json");
        GeoMapCollection geoMapCollection = myDataSource.getData();
        responseMap.put("response_type", "success");
        responseMap.put("geoResult", geoMapCollection.features);
      } else if (minLat == null || maxLat == null || minLon == null || maxLon == null) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "Missing min lat/lon or max lat/lon");
      } else {
        GeoQuery geoQuery = new GeoQuery(minLat, maxLat, minLon, maxLon);
        Collection<Map<String, Object>> searchResults = cachedGeo.search(geoQuery);
        responseMap = searchResults.iterator().next();
      }
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
