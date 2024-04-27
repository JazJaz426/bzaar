package edu.brown.cs.student.main.caches;

import java.util.List;
import java.util.Objects;

public class ACSQuery {
  private final String stateCode;
  private final String stateName;
  private final String countyCode;
  private final String countyName;
  private final List<String> variableNames;

  /**
   * This class represents an instance of ACSQuery, which contains the state code, state name,
   * county code, county name, variable name of an ACS data query
   */
  public ACSQuery(
      String stateCode,
      String stateName,
      String countyCode,
      String countyName,
      List<String> variableNames) {
    this.stateCode = stateCode;
    this.stateName = stateName;
    this.countyCode = countyCode;
    this.countyName = countyName;
    this.variableNames = variableNames;
  }

  /**
   * Get the state code.
   *
   * @return the state code
   */
  public String getStateCode() {
    return stateCode;
  }

  /**
   * Get the state name.
   *
   * @return the state name
   */
  public String getStateName() {
    return stateName;
  }

  /**
   * Get the county code
   *
   * @return the county code
   */
  public String getCountyCode() {
    return countyCode;
  }

  /**
   * Get the county name
   *
   * @return the county name
   */
  public String getCountyName() {
    return countyName;
  }

  /**
   * Get the variable name.
   *
   * @return the variable name
   */
  public List<String> getVariableNames() {
    return variableNames;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ACSQuery acsQuery = (ACSQuery) o;
    return Objects.equals(stateCode, acsQuery.stateCode)
        && Objects.equals(stateName, acsQuery.stateName)
        && Objects.equals(countyCode, acsQuery.countyCode)
        && Objects.equals(countyName, acsQuery.countyName)
        && Objects.equals(variableNames, acsQuery.variableNames);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stateCode, stateName, countyCode, countyName, variableNames);
  }
}
