package edu.brown.cs.student.main.server.handlers;

import com.google.gson.Gson;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetUserProfileHandler implements Route {
  private static final Logger logger = LoggerFactory.getLogger(GetUserProfileHandler.class);
  private static final Gson gson = new Gson();
  private FirebaseUtilities firebaseUtils;

  public GetUserProfileHandler(FirebaseUtilities firebaseUtils) {
    this.firebaseUtils = firebaseUtils;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String email = request.queryParams("email");
    String uid = request.queryParams("uid");
    System.out.println("Email: " + email + ", UID: " + uid);
    try {
      List<Map<String, Object>> userData = firebaseUtils.getCollection(uid, "users");
      System.out.println(userData);
      if (!userData.isEmpty()) {
        logger.info("User data retrieved: {}", userData.get(0));
        response.status(200);
        response.type("application/json");
        return gson.toJson(userData.get(0));
      } else {
        logger.warn("User not found for email: {}", email);
        response.status(404);
        response.type("text/plain");
        return "User not found";
      }
    } catch (Exception e) {
      logger.error("Error fetching user profile", e);
      response.status(500);
      response.type("text/plain");
      return "Internal Server Error";
    }
  }
}
