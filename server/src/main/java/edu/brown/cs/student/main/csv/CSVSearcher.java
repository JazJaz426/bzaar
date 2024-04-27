package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.common.FactoryFailureException;
import edu.brown.cs.student.main.common.utility;
import edu.brown.cs.student.main.creators.CreatorFromRow;
import edu.brown.cs.student.main.query.Query;
import edu.brown.cs.student.main.query.QueryParser;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVSearcher class that takes in as arguments a Reader, CreatorFromRow<List<String>>, and header
 * boolean. Defines both singular and multi search methods.
 */
public class CSVSearcher {

  private CSVParser<List<String>> parser;
  private List<List<String>> parsedData;
  private boolean headerFlag;
  private String headers;

  /**
   * CSVSearcher constructor
   *
   * @param Reader reader
   * @param CreatorFromRow<List<String>> creator
   * @param boolean headerFlag
   */
  public CSVSearcher(Reader reader, CreatorFromRow<List<String>> creator, Boolean headerFlag) {

    try {
      parser = new CSVParser<>(reader, creator, headerFlag);
      ParseResult parseResult = parser.parse();
      this.headerFlag = headerFlag;
      this.headers = parseResult.getHeaders();
      this.parsedData = parseResult.getData();

    } catch (IOException | FactoryFailureException e) {
      System.err.println("File outside of accessible directory. Exiting.");
      return;
    }
  }

  /**
   * Method for a singular search. The caller can specify a target value and target colum. The
   * function returns a List<String<String>> containing the search results
   *
   * @param String targetVal
   * @param String targetCol
   * @return List<List<String>> search results
   */
  public List<List<String>> search(String targetVal, String targetCol) {
    boolean valFound = false;
    String unifiedTargetVal = utility.unifyString(targetVal);
    List<List<String>> searchResults = new ArrayList<>();
    int colIndex = 0;
    if (targetCol != null) {
      int numCols = this.parsedData.get(0).size();
      colIndex = utility.findColIndex(targetCol, this.headerFlag, this.headers, numCols);
      if (colIndex == -1) {
        return null;
      }
      // search for the target value in the targeted column
      for (List<String> unmergedRow : parsedData) {
        String row = String.join(",", unmergedRow);
        String curCol = utility.unifyString(row.split(",")[colIndex]);
        if (curCol.contains(unifiedTargetVal)) {
          valFound = true;
          System.out.println(row);
          searchResults.add(unmergedRow);
        }
      }
    } else {
      // search for the target value in the entire csv file
      for (List<String> unmergedRow : parsedData) {
        String row = String.join(",", unmergedRow);
        String curRow = utility.unifyString(row);
        if (curRow.contains(unifiedTargetVal)) {
          valFound = true;
          System.out.println(row);
          searchResults.add(unmergedRow);
        }
      }
    }
    if (!valFound) {
      System.err.println(
          "The requested value does not exist in the csv file or in the requested column.");
    }
    return searchResults;
  }

  /**
   * Method for a multi search. The caller can specify queries as an argument. The function returns
   * a List<String<String>> containing the search results
   *
   * @param String queries
   * @return List<List<String>> search results
   */
  public List<List<String>> searchMulti(String queries) {
    boolean valFound = false;
    Query query = QueryParser.parse(queries, this.headers, this.headerFlag);
    // create an array to hold the search results
    List<List<String>> searchResults = new ArrayList<>();
    for (List<String> unmergedRow : parsedData) {
      String row = String.join(",", unmergedRow);
      String[] rowArray = row.split(",");
      if (query.contains(rowArray)) {
        valFound = true;
        System.out.println(row);
        searchResults.add(unmergedRow);
      }
    }
    if (!valFound) {
      System.err.println(
          "The requested value does not exist in the csv file or in the requested column.");
    }
    return searchResults;
  }
}
