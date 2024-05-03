package edu.brown.cs.student.main.server;

import static spark.Spark.after;
import static spark.Spark.options;

import edu.brown.cs.student.main.server.handlers.GetClaimListHandler;
import edu.brown.cs.student.main.server.handlers.GetItemsHandler;
import edu.brown.cs.student.main.server.handlers.GetSellerProfileHandler;
import edu.brown.cs.student.main.server.handlers.GetUserProfileHandler;
import edu.brown.cs.student.main.server.handlers.GetWatchListHandler;
import edu.brown.cs.student.main.server.handlers.ModifyClaimListHandler;
import edu.brown.cs.student.main.server.handlers.ModifyWatchListHandler;
import edu.brown.cs.student.main.server.handlers.PostItemHandler;
import edu.brown.cs.student.main.server.handlers.RecordUserActivityHandler;
import edu.brown.cs.student.main.server.handlers.SearchItemsHandler;
import edu.brown.cs.student.main.server.handlers.UpdateItemHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import spark.Spark;

/** Top Level class for our project, utilizes spark to create and maintain our server. */
public class Server {

  public static void setUpServer() {
    int port = 3232;
    Spark.port(port);

    // Enable CORS
    options(
        "/*",
        (request, response) -> {
          String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
          if (accessControlRequestHeaders != null) {
            response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
          }

          String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
          if (accessControlRequestMethod != null) {
            response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
          }

          return "OK";
        });

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
          response.header(
              "Access-Control-Allow-Headers",
              "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
          response.header("Access-Control-Allow-Credentials", "true");
        });

    try {
      FirebaseUtilities firebaseUtils = new FirebaseUtilities();
      Spark.get("/getUserProfile", new GetUserProfileHandler(firebaseUtils));
      Spark.get("/getSellerProfile", new GetSellerProfileHandler(firebaseUtils));
      Spark.get("/getItems", new GetItemsHandler(firebaseUtils));
      Spark.get("/claimItem", new UpdateItemHandler(firebaseUtils));
      Spark.get("/recordUserActivity", new RecordUserActivityHandler(firebaseUtils));
      Spark.get("/modifyWatchList", new ModifyWatchListHandler(firebaseUtils));
      Spark.get("/getWatchList", new GetWatchListHandler(firebaseUtils));
      Spark.get("/modifyClaimList", new ModifyClaimListHandler(firebaseUtils));
      Spark.get("/getClaimList", new GetClaimListHandler(firebaseUtils));
      Spark.get("/searchItems", new SearchItemsHandler(firebaseUtils));
      Spark.post("/postItem", new PostItemHandler(firebaseUtils));
      Spark.init();
      Spark.awaitInitialization();
      System.out.println("Server started at http://localhost:" + port);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Error initializing Firebase: " + e);
      System.exit(1);
    }
  }

  /**
   * Runs Server.
   *
   * @param args none
   */
  public static void main(String[] args) {
    setUpServer();
  }
}
