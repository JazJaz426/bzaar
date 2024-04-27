package edu.brown.cs.student.main.common;

import edu.brown.cs.student.main.csv.ParseResult;

/**
 * Class storing information regarding the shared state of a CSV such as: isFileLoaded, parseResult,
 * headerFlag, fileName
 */
public class CSVSharedVar {
  private boolean isFileLoaded = false;
  private ParseResult parseResult = null;
  private boolean headerFlag = false;
  private String fileName = null;

  // getter and setter methods
  public boolean isFileLoaded() {
    return isFileLoaded;
  }

  /**
   * Updates isFileLoaded boolean
   *
   * @param boolean (fileLoaded)
   */
  public void setFileLoaded(boolean fileLoaded) {
    isFileLoaded = fileLoaded;
  }

  /**
   * Allows for access to parsedResult
   *
   * @return parseResult
   */
  public ParseResult getParseResult() {
    return parseResult;
  }

  /**
   * Updates parsedResult and stores in parseResult variable
   *
   * @param parseResult of type ParseResult
   */
  public void setParseResult(ParseResult parseResult) {
    this.parseResult = parseResult;
  }

  /**
   * Returns header flag
   *
   * @return boolean headerFlag
   */
  public boolean getHeaderFlag() {
    return headerFlag;
  }

  /**
   * Updates header flag
   *
   * @param booleam headerFlag
   */
  public void setHeaderFlag(boolean headerFlag) {
    this.headerFlag = headerFlag;
  }

  /**
   * Accessor method for filepath
   *
   * @return name of file
   */
  public String getFilePath() {
    return fileName;
  }

  /**
   * Updates file path
   *
   * @param String name of file
   */
  public void setFilePath(String fileName) {
    this.fileName = fileName;
  }
}
