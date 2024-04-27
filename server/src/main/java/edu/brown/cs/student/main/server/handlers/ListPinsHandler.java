package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListPinsHandler implements Route {

  public StorageInterface storageHandler;

  public ListPinsHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Invoked when a request is made on this route's corresponding path e.g. '/hello'
   *
   * <p>Fetches all of a user's pins from their firestore db to display on page start
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return The content to be set in the response
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String uid = request.queryParams("uid");

      System.out.println("listing pins for user: " + uid);

      // get all the pins for the user
      List<Map<String, Object>> vals = this.storageHandler.getCollection(uid, "pins");
      System.out.println("collection for user:" + vals.toString());
      // convert the key,value map to just a list of the pins.
      List<List<String>> pinList = new ArrayList<>();
      for (Map<String, Object> pin : vals) {
        String latitude = pin.get("latitude").toString();
        String longitude = pin.get("longitude").toString();
        List<String> coords = new ArrayList<>();
        coords.add(latitude);
        coords.add(longitude);
        pinList.add(coords);
      }

      responseMap.put("response_type", "success");
      responseMap.put("pins", pinList);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
