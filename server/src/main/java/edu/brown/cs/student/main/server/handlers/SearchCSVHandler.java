package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.common.CSVSharedVar;
import edu.brown.cs.student.main.common.ServerAPI;
import edu.brown.cs.student.main.creators.StringCreatorFromRow;
import edu.brown.cs.student.main.csv.CSVSearcher;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * The SearchCSVHandler class creates an instance of CSVSearcher, provides the query parameters and
 * stores the search result in a response map
 */
public class SearchCSVHandler implements Route {

  private final edu.brown.cs.student.main.common.CSVSharedVar CSVSharedVar;

  /**
   * SearchCSVHandler constructor
   *
   * @param CSVSharedVar sharedVar
   */
  public SearchCSVHandler(CSVSharedVar sharedVar) {
    this.CSVSharedVar = sharedVar;
  }

  /**
   * Handles the request to search a CSV file. The request should contain parameters for the search
   * value, column identifier, multi flag and queries. Responds with a JSON object.
   *
   * @param request (contains: searchVal, colIdentifier, multiflag, queries)
   * @param response
   * @return JSON response string
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    if (!CSVSharedVar.isFileLoaded()) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "No file loaded");
      return ServerAPI.serializeResponse(responseMap);
    }
    String searchVal = request.queryParams("val");
    String colIdentifier = request.queryParams("col");
    String multiflag = request.queryParams("multi");
    String queries = request.queryParams("queries");
    if (multiflag == null) {
      multiflag = "false";
    } else if (!multiflag.equals("true") && !multiflag.equals("false")) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "multi flag should be true or false");
      return ServerAPI.serializeResponse(responseMap);
    }
    if (multiflag.equals("true")) {
      if (queries == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "No queries provided");
        return ServerAPI.serializeResponse(responseMap);
      }
    } else {
      if (searchVal == null) {
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "No search value provided");
        return ServerAPI.serializeResponse(responseMap);
      }
    }

    try {
      CSVSearcher searcher =
          new CSVSearcher(
              new FileReader(CSVSharedVar.getFilePath()),
              new StringCreatorFromRow() {},
              CSVSharedVar.getHeaderFlag());
      List<List<String>> searchResult;
      if (Boolean.parseBoolean(multiflag)) {
        searchResult = searcher.searchMulti(queries);
      } else {
        searchResult = searcher.search(searchVal, colIdentifier);
      }
      if (CSVSharedVar.getHeaderFlag() != false) {
        responseMap.put("header", CSVSharedVar.getParseResult().getHeaders());
      }
      responseMap.put("result", "success");
      responseMap.put("data", searchResult);
      return ServerAPI.serializeResponse(responseMap);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "error_datasource");
      responseMap.put("message", "Error happens when searching" + e.toString());
      return ServerAPI.serializeResponse(responseMap);
    }
  }
}
