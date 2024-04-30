package edu.brown.cs.student.main.server.handlers;

import com.google.gson.Gson;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetUserProfileHandler implements Route {
  private static final Logger logger = LoggerFactory.getLogger(GetUserProfileHandler.class);
  private static final Gson gson = new Gson();
  private StorageInterface storage;

  public GetUserProfileHandler(StorageInterface storage) {
    this.storage = storage;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    // String email = request.queryParams("email");
    // String uid = request.queryParams("uid");
    // System.out.println("Email: " + email + ", UID: " + uid);
    // try {
    //   List<Map<String, Object>> userData = firebaseUtils.getCollection(uid, "users");
    //   System.out.println(userData);
    //   if (!userData.isEmpty()) {
    //     logger.info("User data retrieved: {}", userData.get(0));
    //     response.status(200);
    //     response.type("application/json");
    //     return gson.toJson(userData.get(0));
    //   } else {
    //     logger.warn("User not found for email: {}", email);
    //     response.status(404);
    //     response.type("text/plain");
    //     return "User not found";
    //   }
    // } catch (Exception e) {
    //   logger.error("Error fetching user profile", e);
    //   response.status(500);
    //   response.type("text/plain");
    //   return "Internal Server Error";
    // }
    String email = request.queryParams("email");
    String userId = request.queryParams("userId");
    if (email == null && userId == null) {
      response.status(400);
      return "User email or user ID is required";
    }

    Map<String, Object> userProfile = null;
    if (email != null) {
      userProfile = storage.getUserDocumentByEmail(email);
    } else if (userId != null) {
      userProfile = storage.getUserDocumentById(userId);
    }
    if (userProfile == null) {
      response.status(404);
      return "User not found";
    }

    // Convert the user profile map to JSON or directly return it based on your framework's
    // capabilities
    response.status(200);
    response.type("application/json");
    return new Gson().toJson(userProfile);
  }
}
