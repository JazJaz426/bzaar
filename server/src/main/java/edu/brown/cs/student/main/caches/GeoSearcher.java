package edu.brown.cs.student.main.caches;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.geomap.GeoMapCollection.GeoMapCollection;
import edu.brown.cs.student.main.geomap.GeoMapCollection.JSONParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoSearcher implements Searcher<Map<String, Object>, GeoQuery> {

  Map<String, Object> responseData = new HashMap<>();
  List<String> notFoundVariables = new ArrayList<>();
  Moshi moshi = new Moshi.Builder().build();
  JsonAdapter<List<List<String>>> jsonAdapter =
      moshi.adapter(Types.newParameterizedType(List.class, List.class, String.class));

  @Override
  public Collection<Map<String, Object>> search(GeoQuery geoQuery)
      throws IOException, InterruptedException, URISyntaxException {
    // check if the provided min and max lat and lon are valid double string
    JSONParser myDataSource = new JSONParser("./data/geojson/fulldownload.json");
    GeoMapCollection geoMapCollection = myDataSource.getData();

    if (!geoQuery.getMinLat().matches("-?\\d+(\\.\\d+)?")
        || !geoQuery.getMaxLat().matches("-?\\d+(\\.\\d+)?")
        || !geoQuery.getMinLon().matches("-?\\d+(\\.\\d+)?")
        || !geoQuery.getMaxLon().matches("-?\\d+(\\.\\d+)?")) {
      responseData.put("response_type", "failure");
      responseData.put("error", "Invalid min/max lat/lon");
      return List.of(responseData);
    }
    Double minLatDouble = Double.parseDouble(geoQuery.getMinLat());
    Double maxLatDouble = Double.parseDouble(geoQuery.getMaxLat());
    Double minLonDouble = Double.parseDouble(geoQuery.getMinLon());
    Double maxLonDouble = Double.parseDouble(geoQuery.getMaxLon());
    // make sure max is greater than min
    if (minLatDouble >= maxLatDouble || minLonDouble >= maxLonDouble) {
      responseData.put("response_type", "failure");
      responseData.put("error", "Invalid min/max lat/lon");
      return List.of(responseData);
    }
    Map<String, Object> responseData = new HashMap<>();
    var result =
        geoMapCollection.filterGeoJSONByBounds(
                minLatDouble, maxLatDouble, minLonDouble, maxLonDouble)
            .features;
    responseData.put("geoResult", result);
    responseData.put("response_type", "success");
    return List.of(responseData);
  }
}
