package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.net.HttpURLConnection;
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
      response.status(HttpURLConnection.HTTP_BAD_REQUEST);
      responseMap.put("status", HttpURLConnection.HTTP_BAD_REQUEST);
      responseMap.put("message", "User ID is required");
      return Utils.toMoshiJson(responseMap);
    }

    Map<String, Object> userProfile = storage.getUserDocumentById(userId);
    if (userProfile == null) {
      response.status(HttpURLConnection.HTTP_NOT_FOUND);
      responseMap.put("status", HttpURLConnection.HTTP_NOT_FOUND);
      responseMap.put("message", "User not found");
      return Utils.toMoshiJson(responseMap);
    }

    response.status(HttpURLConnection.HTTP_OK);
    responseMap.put("status", HttpURLConnection.HTTP_OK);
    responseMap.put("data", userProfile);
    return Utils.toMoshiJson(responseMap);
  }
}
