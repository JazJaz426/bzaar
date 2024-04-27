package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class RemovePinHandler implements Route {

  public StorageInterface storageHandler;

  public RemovePinHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Invoked when a request is made on this route's corresponding path e.g. '/hello' Removes a pin
   * from the user's db with the corresponding lat/long
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return The content to be set in the response
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // collect parameters from the request
      String uid = request.queryParams("uid");
      String lat = request.queryParams("lat");
      String lon = request.queryParams("lon");

      Map<String, Object> data = new HashMap<>();
      data.put("latitude", lat);
      data.put("longitude", lon);

      // use the storage handler to add the document to the database
      this.storageHandler.removeDocument(uid, "pins", data);

      responseMap.put("response_type", "success");
      responseMap.put("rm-latitude", lat);
      responseMap.put("rm-longitude", lon);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
