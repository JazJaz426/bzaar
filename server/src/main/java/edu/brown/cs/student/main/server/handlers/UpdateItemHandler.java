package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class UpdateItemHandler implements Route {
  private StorageInterface firebaseUtilities;

  public UpdateItemHandler(StorageInterface firebaseUtilities) {
    this.firebaseUtilities = firebaseUtilities;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String itemId = request.queryParams("itemId");
      String claimerId = request.queryParams("claimerId");
      if (itemId == null) {
        responseMap.put("status", 400);
        responseMap.put("message", "Item ID is required.");
        return Utils.toMoshiJson(responseMap);
      }
      firebaseUtilities.updateItemStatus(itemId, claimerId, "claimed");
      if (claimerId == null) {
        responseMap.put("status", 400);
        responseMap.put("message", "Claimer ID is required.");
        return Utils.toMoshiJson(responseMap);
      }
      responseMap.put("status", 200);
      responseMap.put("message", "Item status updated successfully.");
    } catch (Exception e) {
      responseMap.put("status", 500);
      responseMap.put("message", "Failed to update item status: " + e.getMessage());
    }
    return Utils.toMoshiJson(responseMap);
  }
}
