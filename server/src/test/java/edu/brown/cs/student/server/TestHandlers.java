package edu.brown.cs.student.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
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
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import spark.Spark;

public class TestHandlers {
    private final JsonAdapter<Map<String, Object>> adapter;
    private final String uid = "eUTksfsIWnp1qB5xWIBs";
    private final String itemId = "0h1qVgjXsqgFGiSZD0wc";
//    private final String userId = "validUserId";
    static FirebaseUtilities firebaseUtils;

    /**
     * Constructs a new TestHandlers instance, initializing the JSON adapter for parsing responses.
     */
    public TestHandlers() {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
        adapter = moshi.adapter(type);
    }

    /**
     * Sets up the environment before all tests, including starting a mock Spark server and
     * initializing Firebase utilities.
     *
     * @throws IOException if an I/O error occurs.
     */
    @BeforeAll
    public static void setup_before_everything() throws IOException {
        Spark.port(0);
        Logger.getLogger("").setLevel(Level.WARNING); // Suppresses unnecessary logging
        firebaseUtils = new FirebaseUtilities();
    }

    /**
     * Prepares the server before each test by mapping handler routes.
     *
     * @throws IOException if an I/O error occurs.
     */
    @BeforeEach
    public void setup() throws IOException, ExecutionException, InterruptedException {
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
                // Mock the FirebaseUtilities to return a valid claim list
        Spark.init();
        Spark.awaitInitialization(); // Ensures server is ready before proceeding
    }
    /** Cleans up after each test by unmapping handler routes and stopping the server. */
    @AfterEach
    public void teardown() {
        Spark.unmap("/getUserProfile");
        Spark.unmap("/getSellerProfile");
        Spark.unmap("/getItems");
        Spark.unmap("/claimItem");
        Spark.unmap("/recordUserActivity");
        Spark.unmap("/modifyWatchList");
        Spark.unmap("/getWatchList");
        Spark.unmap("/modifyClaimList");
        Spark.unmap("/getClaimList");
        Spark.unmap("/searchItems");
        Spark.unmap("/postItem");
        Spark.awaitStop(); // Ensures server has stopped before proceeding
    }

    /**
     * Attempts to make a GET request to the specified API endpoint.
     *
     * @param apiCall The API endpoint to call.
     * @return The connection to the server.
     * @throws IOException if an I/O error occurs.
     */
    private static HttpURLConnection tryRequest(String apiCall) throws IOException {
        String url = "http://localhost:" + Spark.port() + "/" + apiCall;
        System.out.println(url); // Logs the request URL for debugging
        URL requestURL = new URL(url);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

        clientConnection.setRequestMethod("GET");

        clientConnection.connect();
        return clientConnection;
    }
    @Test
    public void testGetClaimListSuccess()
        throws IOException, ExecutionException, InterruptedException {
        HttpURLConnection connection = tryRequest("getClaimList?userId=" + this.uid);
        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
    }

    @Test
    public void testGetClaimListFailureMissingUserId() throws IOException {
        // Perform the request without a userId
        HttpURLConnection connection = tryRequest("getClaimList");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, connection.getResponseCode());
    }

    @Test
    public void testGetWatchListSuccess() throws IOException {
        HttpURLConnection connection = tryRequest("getWatchList?userId=" + this.uid);
        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
    }

    @Test
    public void testGetWatchListFailureMissingUserId() throws IOException {
        HttpURLConnection connection = tryRequest("getWatchList");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, connection.getResponseCode());
    }

    @Test
    public void testGetItemsHandlerSuccess() throws IOException {
        HttpURLConnection connection = tryRequest("getItems?itemId=" + this.itemId);
        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
        connection = tryRequest("getItems?userId=" + this.uid);
        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
        connection = tryRequest("getItems");
        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
    }

    @Test
    public void testSearchItemsHandlerSuccess() throws IOException {
        // Test with a valid keyword
        HttpURLConnection connection = tryRequest("searchItems?keyword=desk");
        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());

    }

    @Test
    public void testSearchItemsHandlerFailureMissingKeyword() throws IOException {
        // Test with missing keyword
        HttpURLConnection connection = tryRequest("searchItems");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, connection.getResponseCode());

    }



    




 

}
