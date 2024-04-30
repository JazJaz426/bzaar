package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetSellerProfileHandler implements Route {
  private StorageInterface storage;

  public GetSellerProfileHandler(StorageInterface storage) {
    this.storage = storage;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String userId = request.queryParams("userId");
    Map<String, Object> responseMap = new HashMap<>();
    if (userId == null) {
      responseMap.put("status", 400);
      responseMap.put("message", "User ID is required");
      return Utils.toMoshiJson(responseMap);
    }

    Map<String, Object> userProfile = storage.getUserDocumentById(userId);
    if (userProfile == null) {
      responseMap.put("status", 404);
      responseMap.put("message", "User not found");
      return Utils.toMoshiJson(responseMap);
    }

    responseMap.put("status", 200);
    responseMap.put("data", userProfile);
    return Utils.toMoshiJson(responseMap);
  }
}
