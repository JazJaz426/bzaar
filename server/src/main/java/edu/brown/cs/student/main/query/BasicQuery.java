package edu.brown.cs.student.main.query;

import edu.brown.cs.student.main.common.utility;

/**
 * This class extends the Query class and defines the contains(String[] row) method for a Basic
 * Query given a column identifier, value, headers, headerFlag and negation boolean
 */
public class BasicQuery extends Query {
  private Integer colIndex;
  private String value;
  private boolean negation;

  /**
   * Basic Query constructor
   *
   * @param String colIdentifier
   * @param String value
   * @param String headers
   * @param boolean headerFlag
   * @param boolean negation
   */
  public BasicQuery(
      String colIdentifier, String value, String headers, boolean headerFlag, boolean negation) {
    // check if colIdentifier is a string number
    if (utility.isNumeric(colIdentifier)) {
      this.colIndex = Integer.parseInt(colIdentifier);
    } else if (value == null || headers == null) {
      throw new IllegalArgumentException("Error: null argument");
    } else {
      this.colIndex = utility.findColIndex(colIdentifier, headerFlag, headers, value.length());
    }
    this.value = value;
    this.negation = negation;
  }

  /**
   * Defines the contains method for a basic query. The method takes in a String[] row, and checks
   * whether it contains the target query value
   *
   * @param String[] row
   * @return boolean
   */
  public boolean contains(String[] row) {
    if (colIndex == null) {
      String searhString = String.join(",", row);
      if (negation) {
        return !searhString.contains(value);
      }
      // turns the row into a string and checks if the value is in the string
      return searhString.contains(value);
    }
    if (negation) {
      return !row[colIndex].contains(value);
    }
    return row[colIndex].contains(value);
  }
}
