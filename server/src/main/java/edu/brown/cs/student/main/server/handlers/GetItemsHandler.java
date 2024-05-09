package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetItemsHandler implements Route {

  private final StorageInterface firebaseUtilities;

  public GetItemsHandler(StorageInterface firebaseUtilities) {
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
        responseMap.put("status", HttpURLConnection.HTTP_OK);
        responseMap.put("items", items);
        response.status(HttpURLConnection.HTTP_OK);
        return Utils.toMoshiJson(responseMap);
      } catch (Exception e) {
        responseMap.put("status", HttpURLConnection.HTTP_INTERNAL_ERROR);
        responseMap.put("message", "Fail to get all items: " + e.getMessage());
        response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
        return Utils.toMoshiJson(responseMap);
      }
    } else if (itemId != null) {
      try {
        Map<String, Object> item = this.firebaseUtilities.getItemDetails(itemId);
        responseMap.put("status", HttpURLConnection.HTTP_OK);
        responseMap.put("data", item);
        response.status(HttpURLConnection.HTTP_OK);
        return Utils.toMoshiJson(responseMap);
      } catch (Exception e) {
        responseMap.put("status", HttpURLConnection.HTTP_INTERNAL_ERROR);
        responseMap.put("message", "Fail to get item: " + e.getMessage());
        response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
        return Utils.toMoshiJson(responseMap);
      }
    } else if (userId != null) {
      try {

        List<Map<String, Object>> items = this.firebaseUtilities.getItemsByUser(userId);
        responseMap.put("status", HttpURLConnection.HTTP_OK);
        responseMap.put("items", items);
        response.status(HttpURLConnection.HTTP_OK);
        return Utils.toMoshiJson(responseMap);
      } catch (Exception e) {
        responseMap.put("status", HttpURLConnection.HTTP_INTERNAL_ERROR);
        responseMap.put("message", "Fail to get items by user: " + e.getMessage());
        response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
        return Utils.toMoshiJson(responseMap);
      }
    }
    return new NotImplementedException("not implemented");
  }
}
