package edu.brown.cs.student.main.caches;

import java.util.Objects;

public class GeoQuery {
  private final String minLat;
  private final String maxLat;
  private final String minLon;
  private final String maxLon;

  public GeoQuery(String minLat, String maxLat, String minLon, String maxLon) {
    this.minLat = minLat;
    this.maxLat = maxLat;
    this.minLon = minLon;
    this.maxLon = maxLon;
  }

  public String getMinLat() {
    return minLat;
  }

  public String getMaxLat() {
    return maxLat;
  }

  public String getMinLon() {
    return minLon;
  }

  public String getMaxLon() {
    return maxLon;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeoQuery geoquery = (GeoQuery) o;
    return minLat.equals(geoquery.minLat)
        && maxLat.equals(geoquery.maxLat)
        && minLon.equals(geoquery.minLon)
        && maxLon.equals(geoquery.maxLon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(minLat, maxLat, minLon, maxLon);
  }
}
