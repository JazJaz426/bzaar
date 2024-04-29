// package edu.brown.cs.student.main;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

// import com.google.common.cache.CacheStats;
// import com.squareup.moshi.JsonAdapter;
// import com.squareup.moshi.Moshi;
// import com.squareup.moshi.Types;
// import edu.brown.cs.student.main.caches.CachedGeo;
// import edu.brown.cs.student.main.caches.GeoQuery;
// import edu.brown.cs.student.main.caches.GeoSearcher;
// import edu.brown.cs.student.main.server.handlers.GeoJSONHandler;
// import java.io.IOException;
// import java.lang.reflect.Type;
// import java.net.HttpURLConnection;
// import java.net.URISyntaxException;
// import java.net.URL;
// import java.util.Collection;
// import java.util.List;
// import java.util.Map;
// import java.util.logging.Level;
// import java.util.logging.Logger;
// import okio.Buffer;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import spark.Spark;

// public class TestGeoJSONHandler {
//   private JsonAdapter<Map<String, Object>> adapter;

//   public TestGeoJSONHandler() {
//     Moshi moshi = new Moshi.Builder().build();
//     Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
//     adapter = moshi.adapter(type);
//   }

//   @BeforeAll
//   public static void setup_before_everything() {
//     Spark.stop();
//     Spark.port(0);
//     Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
//   }

//   /**
//    * Shared state for all tests. We need to be able to mutate it (adding recipes etc.) but never
//    * need to replace the reference itself. We clear this state out after every test runs.
//    */
//   @BeforeEach
//   public void setup() {
//     // In fact, restart the entire Spark server for every test!
//     GeoSearcher geoSearcher = new GeoSearcher();
//     CachedGeo cachedGeo = new CachedGeo(geoSearcher, 100, 60);
//     Spark.get("geoquery", new GeoJSONHandler(cachedGeo));
//     Spark.init();
//     Spark.awaitInitialization(); // don't continue until the server is listening
//   }

//   @AfterEach
//   public void teardown() {
//     // Gracefully stop Spark listening on both endpoints after each test
//     Spark.unmap("geoquery");
//     Spark.awaitStop();
//   }

//   private static HttpURLConnection tryRequest(String apiCall) throws IOException {
//     URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
//     HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
//     clientConnection.setRequestMethod("GET");
//     clientConnection.connect();
//     return clientConnection;
//   }

//   @Test
//   public void testNoQueryProvided() throws Exception {
//     HttpURLConnection clientConnection1 = tryRequest("geoquery");
//     String jsonResponse = new Buffer().readFrom(clientConnection1.getInputStream()).readUtf8();
//     Map<String, Object> response = adapter.fromJson(jsonResponse);
//     assertEquals("success", response.get("response_type"));
//     assertNotNull(response.get("geoResult"), "The response data should not be null.");
//   }

//   @Test
//   public void testInvalidQueryProvided() throws Exception {
//     HttpURLConnection clientConnection1 =
//         tryRequest("geoquery?minLat=33&maxLat=30&minLon=-80&&maxLon=-70");
//     String jsonResponse = new Buffer().readFrom(clientConnection1.getInputStream()).readUtf8();
//     Map<String, Object> response = adapter.fromJson(jsonResponse);
//     assertEquals("failure", response.get("response_type"));
//     assertNotNull(response.get("error"), "Invalid min/max lat/lon");
//   }

//   @Test
//   public void testMissingQueryProvided() throws Exception {
//     HttpURLConnection clientConnection1 = tryRequest("geoquery?minLat=33&maxLat=30&minLon=-80");
//     String jsonResponse = new Buffer().readFrom(clientConnection1.getInputStream()).readUtf8();
//     Map<String, Object> response = adapter.fromJson(jsonResponse);
//     assertEquals("failure", response.get("response_type"));
//     assertNotNull(response.get("error"), "Missing min lat/lon or max lat/lon");
//   }

//   @Test
//   public void testQueryProvided() throws Exception {
//     HttpURLConnection clientConnection1 =
//         tryRequest("geoquery?minLat=33&maxLat=43&minLon=-80&maxLon=-70");
//     String jsonResponse = new Buffer().readFrom(clientConnection1.getInputStream()).readUtf8();
//     Map<String, Object> response = adapter.fromJson(jsonResponse);
//     assertEquals("success", response.get("response_type"));
//     assertNotNull(response.get("geoResult"), "The response data should not be null.");
//     // check if georesult is an empty list
//     List<Object> geoResultList = (List<Object>) response.get("geoResult");
//     assertNotEquals(0, geoResultList.size(), "geoResult should not be empty.");
//   }

//   @Test
//   public void testCachedQuery() throws Exception {
//     String minLat = "32";
//     String maxLat = "44";
//     String minLon = "-80";
//     String maxLon = "-70";
//     GeoSearcher geoSearcher = new GeoSearcher();
//     CachedGeo cachedGeo = new CachedGeo(geoSearcher, 100, 60);
//     GeoQuery geoQuery = new GeoQuery(minLat, maxLat, minLon, maxLon);
//     CacheStats stats = cachedGeo.getCacheStats();
//     assertEquals(0, stats.hitCount());
//     cachedGeo.search(geoQuery);
//     stats = cachedGeo.getCacheStats();
//     assertEquals(0, stats.hitCount());
//     cachedGeo.search(geoQuery);
//     stats = cachedGeo.getCacheStats();
//     assertEquals(1, stats.hitCount());
//     GeoQuery geoQuery2 = new GeoQuery("33", "43", "-81", "-70");
//     cachedGeo.search(geoQuery2);
//     stats = cachedGeo.getCacheStats();
//     assertEquals(1, stats.hitCount());
//   }

//   // unit test for the GeoQuery class
//   @Test
//   public void testGeoQuery() {
//     GeoQuery geoQuery = new GeoQuery("32", "44", "-80", "-70");
//     assertEquals("32", geoQuery.getMinLat());
//     assertEquals("44", geoQuery.getMaxLat());
//     assertEquals("-80", geoQuery.getMinLon());
//     assertEquals("-70", geoQuery.getMaxLon());
//   }

//   // unit test for GeoSearcher
//   @Test
//   public void testGeoSearcher() throws IOException, URISyntaxException, InterruptedException {
//     GeoSearcher geoSearcher = new GeoSearcher();
//     GeoQuery geoQuery = new GeoQuery("32", "44", "-80", "-70");
//     Collection geoResult = geoSearcher.search(geoQuery);
//     assertNotNull(geoResult);
//   }
// }
