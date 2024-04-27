package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.common.FactoryFailureException;
import java.util.List;

public abstract class StringCreatorFromRow implements CreatorFromRow<List<String>> {

  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    try {
      return row;
    } catch (Exception e) {
      throw new FactoryFailureException("Failed to create String from row", (List<String>) e);
    }
  }
}
