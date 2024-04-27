package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.SearchAreaHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestSearchAreaHandler {
  private JsonAdapter<Map<String, Object>> adapter;

  public TestSearchAreaHandler() {
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    adapter = moshi.adapter(type);
  }

  @BeforeAll
  public static void setup_before_everything() throws InterruptedException {
    Spark.awaitInitialization();
    Spark.stop();
    // Give Spark some time to stop. This is not ideal but can be a workaround.
    Thread.sleep(1000); // Adjust time based on actual needs.
    Spark.port(0); // Configure Spark to use a random available port.
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  /**
   * Shared state for all tests. We need to be able to mutate it (adding recipes etc.) but never
   * need to replace the reference itself. We clear this state out after every test runs.
   */
  @BeforeEach
  public void setup() {
    // In fact, restart the entire Spark server for every test!

    Spark.get("areaquery", new SearchAreaHandler());
    Spark.init();

    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("areaquery");
    Spark.awaitStop();
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestMethod("GET");
    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testNoKeywordProvided() throws Exception {
    HttpURLConnection clientConnection1 = tryRequest("areaquery");
    String jsonResponse = new Buffer().readFrom(clientConnection1.getInputStream()).readUtf8();
    Map<String, Object> response = adapter.fromJson(jsonResponse);
    assertEquals("failure", response.get("response_type"));
    assertNotNull(response.get("error"), "No keyword provided");
  }

  @Test
  public void testKeywordProvided() throws Exception {
    HttpURLConnection clientConnection1 = tryRequest("areaquery?keyword=Lakeview%20Park");
    String jsonResponse = new Buffer().readFrom(clientConnection1.getInputStream()).readUtf8();
    Map<String, Object> response = adapter.fromJson(jsonResponse);
    assertEquals("success", response.get("response_type"));
    assertNotNull(response.get("geoResult"), "The response data should not be null.");

    List<Object> geoResultList = (List<Object>) response.get("geoResult");
    boolean keywordFound = false;
    for (Object geoResult : geoResultList) {
      Map<String, Object> geoResultMap = (Map<String, Object>) geoResult;
      Map<String, Object> properties = (Map<String, Object>) geoResultMap.get("properties");
      Object areaDescriptionData = properties.get("area_description_data");
      // iterate through the area_description_data to find the keyword
      if (areaDescriptionData.toString().contains("Lakeview")) {
        keywordFound = true;
        break;
      }
    }
    assertTrue("The keyword Lakeview should be found in area_description_data", keywordFound);
  }
}
