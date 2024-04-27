package edu.brown.cs.student.main.query;

/**
 * This class extends the Query class and defines the contains(String[] row) method for the AndQuery
 */
public class AndQuery extends Query {
  private Query[] queries;

  public AndQuery(Query... queries) {
    this.queries = queries;
  }

  @Override
  public boolean contains(String[] row) {
    for (Query query : queries) {
      if (!query.contains(row)) {
        return false;
      }
    }
    return true;
  }
}
