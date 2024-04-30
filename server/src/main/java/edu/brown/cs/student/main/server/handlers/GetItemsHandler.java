package edu.brown.cs.student.main.server.handlers;

import com.google.gson.Gson;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetItemsHandler implements Route {

  private FirebaseUtilities firebaseUtilities;

  public GetItemsHandler(FirebaseUtilities firebaseUtilities) {
    this.firebaseUtilities = firebaseUtilities;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    // Extract itemId from request parameters
    String itemId = request.queryParams("id");
    String uid = request.queryParams("uid");
    Map<String, Object> responseMap = new HashMap<>();
    if (itemId == null && uid == null) {
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
    }
    return new NotImplementedException("not implemented");

//    if (itemId == null || itemId.isEmpty()) {
//      response.status(400); // Bad Request
//      return "Item ID must be provided.";
//    }
//
//    try {
//      // Call FirebaseUtilities to get item details
//      Map<String, Object> itemDetails = firebaseUtilities.getItemDetails(itemId);
//      if (itemDetails != null) {
//        response.status(200); // OK
//        response.type("application/json");
//        return new Gson().toJson(itemDetails);
//      } else {
//        response.status(404); // Not Found
//        return "Item details not found for ID: " + itemId;
//      }
//    } catch (Exception e) {
//      response.status(500); // Internal Server Error
//      return "An error occurred: " + e.getMessage();
//    }

  }
}
