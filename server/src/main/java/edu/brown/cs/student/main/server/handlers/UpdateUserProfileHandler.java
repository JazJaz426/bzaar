package edu.brown.cs.student.main.server.handlers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import spark.Request;
import spark.Response;
import spark.Route;

public class UpdateUserProfileHandler implements Route {
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String token = request.headers("Authorization").substring(7); // Assuming Bearer token
    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
    String userEmail = decodedToken.getEmail();
    // Extract user data from request
    String newName = request.queryParams("name");
    String newEmail = request.queryParams("email");
    String newAddress = request.queryParams("address");

    // Update user data in Firebase
    updateUserProfile(userEmail, newName, newEmail, newAddress);

    response.status(200);
    return "Profile updated successfully";
  }

  private void updateUserProfile(String userEmail, String name, String email, String address) {
    // Logic to update user profile in Firebase
  }
}
