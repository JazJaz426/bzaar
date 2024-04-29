package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handlers.GetUserProfileHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import spark.Filter;
import spark.Spark;

/** Top Level class for our project, utilizes spark to create and maintain our server. */
public class Server {

  public static void setUpServer() {
    int port = 3232;
    Spark.port(port);

    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });

    // StorageInterface firebaseUtils;
    // // CSVSharedVar csvSharedVar = new CSVSharedVar();
    // try {
    //   firebaseUtils = new FirebaseUtilities();
    // Setting up the handler for the GET /order and /activity endpoints
    // Spark.get("add-pin", new AddPinHandler(firebaseUtils));
    // Spark.get("list-pins", new ListPinsHandler(firebaseUtils));
    // Spark.get("rmv-pin", new RemovePinHandler(firebaseUtils));
    // Spark.get("clear-user", new ClearUserHandler(firebaseUtils));
    // GeoSearcher geoSearcher = new GeoSearcher();
    // CachedGeo cachedGeo = new CachedGeo(geoSearcher, 100, 60);
    // Spark.get("geoquery", new GeoJSONHandler(cachedGeo));
    // Spark.get("areaquery", new SearchAreaHandler());
    // Spark.get("loadcsv", new LoadCSVHandler(csvSharedVar));
    // Spark.get("viewcsv", new ViewCSVHandler(csvSharedVar));
    // Spark.get("searchcsv", new SearchCSVHandler(csvSharedVar));
    // ACSSearcher acsSearcher =
    //     new ACSSearcher(); // Assuming default constructor, adjust as necessary
    // CachedACSInfo cachedACSInfo =
    //     new CachedACSInfo(acsSearcher, 100, 60); // Adjust parameters as necessary
    // // Spark.get("broadband", new BroadbandHandler());
    // Spark.get("broadband", new BroadbandHandler(cachedACSInfo));
    // Spark.notFound(
    //     (request, response) -> {
    //       response.status(404); // Not Found
    //       System.out.println("ERROR");
    //       return "404 Not Found - The requested endpoint does not exist.";
    //     });
    try {
      System.out.println("1234");
      FirebaseUtilities firebaseUtils = new FirebaseUtilities();
      Spark.get("/getUserProfile", new GetUserProfileHandler(firebaseUtils));
      System.out.println("1234");
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
