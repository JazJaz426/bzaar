package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchItemsHandler implements Route {
  private final StorageInterface firebaseUtils;

  public SearchItemsHandler(StorageInterface firebaseUtils) {
    this.firebaseUtils = firebaseUtils;
  }

  @Override
  public Object handle(Request request, Response response) {
    String keyword = request.queryParams("keyword");
    if (keyword == null || keyword.trim().isEmpty()) {
      response.status(400);
      return Utils.toMoshiJson(Map.of("status", 400, "error", "Keyword is required"));
    }
    try {
      List<Map<String, Object>> searchResults = firebaseUtils.searchItemsByKeyword(keyword);
      return Utils.toMoshiJson(Map.of("status", 200, "items", searchResults));
    } catch (Exception e) {
      response.status(500);
      return Utils.toMoshiJson(Map.of("status", 500, "error", "Internal server error"));
    }
  }
}
