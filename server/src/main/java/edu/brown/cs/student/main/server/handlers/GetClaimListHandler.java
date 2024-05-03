package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetClaimListHandler implements Route {
  private final FirebaseUtilities firebaseUtils;

  public GetClaimListHandler(FirebaseUtilities firebaseUtils) {
    this.firebaseUtils = firebaseUtils;
  }

  @Override
  public Object handle(Request request, Response response) {
    String userId = request.queryParams("userId");
    if (userId == null || userId.trim().isEmpty()) {
      response.status(400);
      return Utils.toMoshiJson(Map.of("status", 400, "error", "User ID is required"));
    }
    try {
      List<String> claimList = firebaseUtils.getClaimList(userId);
      if (claimList == null) {
        response.status(404);
        return Utils.toMoshiJson(Map.of("status", 500, "error", "Claim list not found"));
      }
      return Utils.toMoshiJson(Map.of("status", 200, "claimlist", claimList));
    } catch (Exception e) {
      response.status(500);
      return Utils.toMoshiJson(
          Map.of("status", 500, "error", "Internal server error: " + e.getMessage()));
    }
  }
}
