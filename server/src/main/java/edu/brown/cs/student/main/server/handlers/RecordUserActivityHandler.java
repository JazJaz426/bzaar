package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class RecordUserActivityHandler implements Route {

  private final FirebaseUtilities firebaseUtilities;

  public RecordUserActivityHandler(FirebaseUtilities firebaseUtilities) {
    this.firebaseUtilities = firebaseUtilities;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    // Extract itemId from request parameters
    String interactiontType = request.queryParams("interaction_type");
    String itemId = request.queryParams("itemId");
    String userId = request.queryParams("userId");
    Map<String, Object> responseMap = new HashMap<>();
    if (interactiontType == null || itemId == null || userId == null) {
      responseMap.put("status", 400);
      responseMap.put("message", "One or more parameters are null");
      return Utils.toMoshiJson(responseMap);
    }

    List<String> validInteractionTypes = Arrays.asList("clicked", "claimed", "liked");
    if (!validInteractionTypes.contains(interactiontType)) {
      responseMap.put("status", 400);
      responseMap.put("message", "Invalid interaction type: " + interactiontType);
      return Utils.toMoshiJson(responseMap);
    }

    try {
      this.firebaseUtilities.recordUserActivity(interactiontType, itemId, userId);
      responseMap.put("status", 200);
      responseMap.put(
          "message",
          "User activity recorded successfully, interaction type: "
              + interactiontType
              + ", item id: "
              + itemId
              + ", user id: "
              + userId);
      return Utils.toMoshiJson(responseMap);
    } catch (Exception e) {
      responseMap.put("status", 500);
      responseMap.put("message", "Failed to record user activity: " + e.getMessage());
      return Utils.toMoshiJson(responseMap);
    }
  }
}
