package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetItemsHandler implements Route {

  private final FirebaseUtilities firebaseUtilities;

  public GetItemsHandler(FirebaseUtilities firebaseUtilities) {
    this.firebaseUtilities = firebaseUtilities;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    // Extract itemId from request parameters
    String itemId = request.queryParams("itemId");
    String userId = request.queryParams("userId");
    Map<String, Object> responseMap = new HashMap<>();
    if (itemId == null && userId == null) {
      try {
        List<Map<String, Object>> items = this.firebaseUtilities.getCollection("items");
        responseMap.put("status", 200);
        responseMap.put("items", items);
        return Utils.toMoshiJson(responseMap);
      } catch (Exception e) {
        responseMap.put("status", 500);
        responseMap.put("message", "Fail to get all items: " + e.getMessage());
        return Utils.toMoshiJson(responseMap);
      }
    } else if (itemId != null) {
      try {
        Map<String, Object> item = this.firebaseUtilities.getItemDetails(itemId);
        responseMap.put("status", 200);
        responseMap.put("data", item);
        return Utils.toMoshiJson(responseMap);
      } catch (Exception e) {
        responseMap.put("status", 500);
        responseMap.put("message", "Fail to get item: " + e.getMessage());
        return Utils.toMoshiJson(responseMap);
      }
    } else if (userId != null) {
      try {
        List<Map<String, Object>> items = this.firebaseUtilities.getItemsByUser(userId);
        responseMap.put("status", 200);
        responseMap.put("items", items);
        return Utils.toMoshiJson(responseMap);
      } catch (Exception e) {
        responseMap.put("status", 500);
        responseMap.put("message", "Fail to get items by user: " + e.getMessage());
        return Utils.toMoshiJson(responseMap);
      }
    }
    return new NotImplementedException("not implemented");
  }
}