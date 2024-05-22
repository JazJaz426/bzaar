package edu.brown.cs.student.main.server.handlers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
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
    String token = request.headers("Authorization").substring(7); // Assuming Bearer token
    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
    String userEmail = decodedToken.getEmail();

    Map<String, Object> responseMap = new HashMap<>();
    try {
      String newName = request.queryParams("name");
      if (newName == null) {
        responseMap.put("status", 400);
        responseMap.put("message", "User name is required.");
        return Utils.toMoshiJson(responseMap);
      }
      String newAddress = request.queryParams("address");
      if (newAddress == null) {
        responseMap.put("status", 400);
        responseMap.put("message", "User address is required.");
        return Utils.toMoshiJson(responseMap);
      }
      System.out.println("userEmail: " + userEmail);
      System.out.println("new Name: " + newName);
      System.out.println("new Address: " + newAddress);
      firebaseUtilities.updateUserProfile(userEmail, newName, newAddress);
      responseMap.put("status", 200);
      responseMap.put("message", "User profile updated successfully.");
    } catch (Exception e) {
      responseMap.put("status", 500);
      responseMap.put("message", "Failed to update user profile: " + e.getMessage());
    }
    return Utils.toMoshiJson(responseMap);
  }
}
