package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetUserProfileHandler implements Route {
  private static final Logger logger = LoggerFactory.getLogger(GetUserProfileHandler.class);
  private StorageInterface storage;

  public GetUserProfileHandler(StorageInterface storage) {
    this.storage = storage;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String email = request.queryParams("email");
    Map<String, Object> responseMap = new HashMap<>();
    if (email == null) {
      response.status(HttpURLConnection.HTTP_BAD_REQUEST);
      responseMap.put("status", HttpURLConnection.HTTP_BAD_REQUEST);
      responseMap.put("message", "User email is required");
      return Utils.toMoshiJson(responseMap);
    }

    Map<String, Object> userProfile = storage.getUserDocumentByEmail(email);
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
