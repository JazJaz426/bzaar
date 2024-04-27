package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.common.FactoryFailureException;
import edu.brown.cs.student.main.common.utility;
import edu.brown.cs.student.main.creators.CreatorFromRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class defines a CSVParser which takes in as arguments a Reader, CreatorFromRow<T> and a
 * header boolean to parse a csv file and return it to the caller in its desired format
 */
public class CSVParser<T> {

  private final CreatorFromRow<T> creator;
  private final Reader reader;
  private final boolean headerFlag;

  /**
   * CSVParser constructor
   *
   * @param Reader reader
   * @param CreatorFromRow<T> creator
   * @param boomean headerFlag
   */
  public CSVParser(Reader reader, CreatorFromRow<T> creator, boolean headerFlag) {
    this.reader = reader;
    this.creator = creator;
    this.headerFlag = headerFlag;
  }

  /**
   * Method that parses the passed in Reader object and converts rows into specified
   * CreatorFromRow<T> type
   *
   * @return ParseResult<T> (parsed result in specified type)
   */
  public ParseResult<T> parse() throws IOException, FactoryFailureException {
    int expectedColumns = -1;
    String line;
    BufferedReader curReader = new BufferedReader(reader);
    List<T> result = new ArrayList<>();
    boolean isFirstRow = true;
    String headers = null;
    while ((line = curReader.readLine()) != null) {

      String[] fields = utility.regexSplitCSVRow.split(line);
      if (expectedColumns == -1) {
        expectedColumns = fields.length;
      } else if (fields.length != expectedColumns) {
        throw new IOException("Inconsistent column count at line: " + line);
      }
      List<String> row =
          Arrays.stream(fields).map(utility::postprocess).collect(Collectors.toList());
      if (isFirstRow && headerFlag) { // Optionally use headers for something
        isFirstRow = false;
        //        CreatorFromRow<List<String>> strCreator = new StringCreatorFromRow() {};
        headers = String.join(",", row);
        T object = creator.create(row);
        result.add(object);
        continue; // Skip adding the header row as a data object
      }
      T object = creator.create(row);
      result.add(object);
    }
    curReader.close();
    return new ParseResult<>(headers, result);
  }

  // purely for test purposes
  public String getFirstLine() throws IOException {
    BufferedReader curReader = new BufferedReader(reader);
    String line = curReader.readLine();
    curReader.close();
    return line;
  }
}
