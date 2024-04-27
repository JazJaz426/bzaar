package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.caches.ACSQuery;
import edu.brown.cs.student.main.caches.CachedACSInfo;
import edu.brown.cs.student.main.common.GetCountyCodes;
import edu.brown.cs.student.main.common.GetStateCodes;
import edu.brown.cs.student.main.common.ServerAPI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** This class implements Route for handling data requests from the broadband endpoint. */
public class BroadbandHandler implements Route {
  private static Map<String, String> stateCodes = null;

  private CachedACSInfo cachedACSInfo;

  /**
   * Broadband Handler constructor that fills out state code map
   *
   * @param CachedACSInfo cachedACSInfo
   */
  public BroadbandHandler(CachedACSInfo cachedACSInfo) {
    try {

      this.cachedACSInfo = cachedACSInfo;
      if (stateCodes == null) {
        stateCodes = GetStateCodes.getStatesCodes();
      }
    } catch (Exception e) {
      e.printStackTrace();
      stateCodes = new HashMap<>();
    }
  }

  /**
   * Handles the HTTP request to retrieve broadband data.
   *
   * @param Request request (contains: county name, statename, ACSVariable (optional))
   * @param Response response
   * @return The JSON response string
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String stateName = request.queryParams("state");
      if (stateName == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("error_message", "Missing 'state' parameter");
        return ServerAPI.serializeResponse(responseMap);
      }
      String countyName = request.queryParams("county");
      if (countyName == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("error_message", "Missing 'county' parameter");
        return ServerAPI.serializeResponse(responseMap);
      }
      String stateCode = stateCodes.getOrDefault(stateName, null);
      if (stateCode == null) {
        responseMap.put("result", "error_datasource");
        responseMap.put(
            "error_message", "Provided state name '%s' doesn't exist".formatted(stateName));
        return ServerAPI.serializeResponse(responseMap);
      }
      String countyCode = GetCountyCodes.getCountyCode(stateCode, countyName);
      if (countyCode == null) {
        responseMap.put("result", "error_datasource");
        responseMap.put(
            "error_message", "Provided county name '%s' doesn't exist".formatted(countyName));
        return ServerAPI.serializeResponse(responseMap);
      }

      // optional
      String variablesParam = request.queryParams("variables");
      if (variablesParam == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("error_message", "Missing 'variables' parameter");
        return ServerAPI.serializeResponse(responseMap);
      }
      List<String> variableNames = new ArrayList<>();
      if (!variablesParam.isEmpty()) {
        variableNames = Arrays.asList(variablesParam.split(","));
      }
      ACSQuery acsQuery = new ACSQuery(stateCode, stateName, countyCode, countyName, variableNames);
      Collection<Map<String, Object>> searchResults = cachedACSInfo.search(acsQuery);
      responseMap = searchResults.iterator().next();
    } catch (IOException e) {
      // Handling IOException specifically
      responseMap.clear();
      responseMap.put("result", "error_datasource");
      responseMap.put("error_message", "Failed to retrieve data from ACS API: " + e.getMessage());
    } catch (Exception e) {
      // Handling other exceptions generically
      responseMap.clear();
      responseMap.put("result", "error_internal");
      responseMap.put("error_message", "An internal error occurred: " + e.getMessage());
    }
    return ServerAPI.serializeResponse(responseMap);
  }

  public record ParseSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public ParseSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }

    /**
     * Serializes response as a json
     *
     * @return serialized json response
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ParseSuccessResponse> adapter = moshi.adapter(ParseSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }
}
