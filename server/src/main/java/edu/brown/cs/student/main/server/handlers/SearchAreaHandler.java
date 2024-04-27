package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.geomap.GeoMapCollection.GeoMapCollection;
import edu.brown.cs.student.main.geomap.GeoMapCollection.JSONParser;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchAreaHandler implements Route {
  @Override
  public Object handle(Request request, Response response) throws FileNotFoundException {
    Map<String, Object> responseMap = new HashMap<>();

    try {
      String keyword = request.queryParams("keyword");
      if (keyword == null) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "No keyword provided");
        return Utils.toMoshiJson(responseMap);
      }
      JSONParser myDataSource = new JSONParser("./data/geojson/fulldownload.json");
      GeoMapCollection geoMapCollection = myDataSource.getData();
      GeoMapCollection filteredGeoMapCollection = geoMapCollection.filterGeoJSONByKeyword(keyword);
      responseMap.put("response_type", "success");
      responseMap.put("geoResult", filteredGeoMapCollection.features);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
