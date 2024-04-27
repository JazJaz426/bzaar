package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.common.CSVSharedVar;
import edu.brown.cs.student.main.common.FactoryFailureException;
import edu.brown.cs.student.main.common.ServerAPI;
import edu.brown.cs.student.main.common.utility;
import edu.brown.cs.student.main.creators.StringCreatorFromRow;
import edu.brown.cs.student.main.csv.CSVParser;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * The LoadCSVHandler class checks whether the input filepath is valid and if so, parses the CSV and
 * stores the data in an instance of CSVSharedVar
 */
public class LoadCSVHandler implements Route {
  private edu.brown.cs.student.main.common.CSVSharedVar CSVSharedVar;

  /**
   * LoadCSVHandler constructor
   *
   * @param CSVSharedVar sharedVar
   */
  public LoadCSVHandler(CSVSharedVar sharedVar) {
    this.CSVSharedVar = sharedVar;
  }

  /**
   * Handles the request to load a CSV file. The request should contain parameters for the file path
   * ('path') and a boolean for whether the file has a header ('hasHeader'). Responds with a JSON
   * object.
   *
   * @param request (contains: filepath and hasHeader boolean)
   * @param response
   * @return JSON response string
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String filepath = request.queryParams("filepath");
    if (filepath == null) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "No filepath provided");
      return ServerAPI.serializeResponse(responseMap);
    }
    CSVSharedVar.setFilePath(filepath);
    String headerFlag = request.queryParams("headerFlag");
    if (headerFlag == null) {
      headerFlag = "false";
    }
    CSVSharedVar.setHeaderFlag(Boolean.parseBoolean(headerFlag));
    ;

    // check if the file path is valid
    if (!utility.isValidPath(filepath)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "Invalid filepath");
      return ServerAPI.serializeResponse(responseMap);
    }
    // create a csv parser and parse the file
    try {
      CSVParser parser =
          new CSVParser<>(
              new FileReader(filepath),
              new StringCreatorFromRow() {},
              CSVSharedVar.getHeaderFlag());
      CSVSharedVar.setParseResult(parser.parse());
      CSVSharedVar.setFileLoaded(true);
      responseMap.put("result", "success");
      return ServerAPI.serializeResponse(responseMap);
    } catch (IOException | FactoryFailureException e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("message", "Error happens when parsing" + e.toString());
      return ServerAPI.serializeResponse(responseMap);
    }
  }
}
