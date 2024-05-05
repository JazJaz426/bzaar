package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.HashMap;
import java.util.Map;
import spark.Route;

public class DeleteItemHandler implements Route {
  private final FirebaseUtilities firebaseUtilities;

  public DeleteItemHandler(FirebaseUtilities firebaseUtilities) {
    this.firebaseUtilities = firebaseUtilities;
  }

  @Override
  public Object handle(spark.Request request, spark.Response response) throws Exception {
    String itemId = request.queryParams("itemId");
    String userId = request.queryParams("userId");
    System.out.println("Deleting item with id: " + itemId + " for user: " + userId);
    Map<String, Object> responseMap = new HashMap<>();
    if (itemId == null) {
      responseMap.put("status", 400);
      responseMap.put("message", "itemId is required");
      return Utils.toMoshiJson(responseMap);
    }
    try {
      this.firebaseUtilities.deleteItem(itemId, userId);
      responseMap.put("status", 200);
      responseMap.put("message", "Item deleted successfully");
      return Utils.toMoshiJson(responseMap);
    } catch (Exception e) {
      System.out.println("Fail to delete item: " + e.getMessage());
      responseMap.put("status", 500);
      responseMap.put("message", "Fail to delete item ");
      return Utils.toMoshiJson(responseMap);
    }
  }
}
