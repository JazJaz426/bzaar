package edu.brown.cs.student.main.server.handlers;

import com.google.gson.Gson;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class UpdateUserProfileHandler implements Route {
  private StorageInterface firebaseUtilities;

  public UpdateUserProfileHandler(StorageInterface firebaseUtilities) {
    this.firebaseUtilities = firebaseUtilities;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String body = request.body();
    Gson gson = new Gson();
    Map<String, String> requestBody = gson.fromJson(body, Map.class);
    Map<String, Object> responseMap = new HashMap<>();
    String email = requestBody.get("email");
    try {
      String newName = requestBody.get("name");
      if (newName == null) {
        responseMap.put("status", 400);
        responseMap.put("message", "User name is required.");
        return Utils.toMoshiJson(responseMap);
      }
      String newAddress = requestBody.get("address");
      if (newAddress == null) {
        responseMap.put("status", 400);
        responseMap.put("message", "User address is required.");
        return Utils.toMoshiJson(responseMap);
      }
      firebaseUtilities.updateUserProfile(email, newName, newAddress);
      responseMap.put("status", 200);
      responseMap.put("message", "User profile updated successfully.");
    } catch (Exception e) {
      responseMap.put("status", 500);
      responseMap.put("message", "Failed to update user profile: " + e.getMessage());
    }
    return Utils.toMoshiJson(responseMap);
  }
}
