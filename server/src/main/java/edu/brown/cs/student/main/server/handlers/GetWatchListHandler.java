package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetWatchListHandler implements Route {
  private final StorageInterface firebaseUtils;

  public GetWatchListHandler(StorageInterface firebaseUtils) {
    this.firebaseUtils = firebaseUtils;
  }

  @Override
  public Object handle(Request request, Response response) {
    String userId = request.queryParams("userId");
    if (userId == null || userId.trim().isEmpty()) {
      response.status(HttpURLConnection.HTTP_BAD_REQUEST);
      return Utils.toMoshiJson(
          Map.of("status", HttpURLConnection.HTTP_BAD_REQUEST, "error", "User ID is required"));
    }
    try {
      List<String> watchList = firebaseUtils.getWatchList(userId);
      if (watchList == null) {
        response.status(404);
        return Utils.toMoshiJson(Map.of("status", 500, "error", "Watch list not found"));
      }
      response.status(HttpURLConnection.HTTP_OK);
      return Utils.toMoshiJson(Map.of("status", HttpURLConnection.HTTP_OK, "watchList", watchList));
    } catch (Exception e) {
      response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
      return Utils.toMoshiJson(
          Map.of(
              "status",
              HttpURLConnection.HTTP_INTERNAL_ERROR,
              "error",
              "Internal server error: " + e.getMessage()));
    }
  }
}
