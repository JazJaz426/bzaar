package edu.brown.cs.student.main.server.handlers;

import com.google.gson.Gson;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewItemsDetailsHandler implements Route {
  private FirebaseUtilities firebaseUtilities;

  public ViewItemsDetailsHandler(FirebaseUtilities firebaseUtilities) {
    this.firebaseUtilities = firebaseUtilities;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    // Extract itemId from request parameters
    String itemId = request.queryParams("id");
    if (itemId == null || itemId.isEmpty()) {
      response.status(400); // Bad Request
      return "Item ID must be provided.";
    }

    try {
      // Call FirebaseUtilities to get item details
      Map<String, Object> itemDetails = firebaseUtilities.getItemDetails(itemId);
      if (itemDetails != null) {
        response.status(200); // OK
        response.type("application/json");
        return new Gson().toJson(itemDetails);
      } else {
        response.status(404); // Not Found
        return "Item details not found for ID: " + itemId;
      }
    } catch (Exception e) {
      response.status(500); // Internal Server Error
      return "An error occurred: " + e.getMessage();
    }
  }
}
