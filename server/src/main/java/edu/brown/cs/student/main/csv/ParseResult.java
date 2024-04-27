package edu.brown.cs.student.main.csv;

import java.util.List;

/**
 * This class defines the ParseResult<T> which stores parsed data and its headers. Has getter
 * methods allowing for its data and headers to be accessed.
 */
public class ParseResult<T> {
  private final String headers;
  private final List<T> data;

  /**
   * ParseResult constructor
   *
   * @param String headers
   * @param List<T> data
   */
  public ParseResult(String headers, List<T> data) {
    this.headers = headers;
    this.data = data;
  }

  /**
   * Getter method for header
   *
   * @return String headers
   */
  public String getHeaders() {
    return headers;
  }

  /**
   * Getter method for data
   *
   * @return List<T> data
   */
  public List<T> getData() {
    return data;
  }
}
