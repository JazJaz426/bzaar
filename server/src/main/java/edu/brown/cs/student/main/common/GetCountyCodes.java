package edu.brown.cs.student.main.common;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class defines methods to get the county code from API Census Data */
public class GetCountyCodes {

  /**
   * Method to get a specific county code by stateCode and countyName
   *
   * @param String stateCode
   * @param String countyName
   * @return String countyCode
   */
  public static String getCountyCode(String stateCode, String countyName)
      throws URISyntaxException, IOException, InterruptedException {
    String responseJson = sendRequest(stateCode);
    Map<String, String> countyCodes = deserializeCountyCodes(responseJson);
    return countyCodes.getOrDefault(countyName, null);
  }

  /**
   * Method that deserializes the data retrieved from the API request
   *
   * @param String json
   * @return Hashmap of countyCodes in the form Map<String, String>
   */
  private static Map<String, String> deserializeCountyCodes(String json) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    Type listType = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> jsonAdapter = moshi.adapter(listType);

    List<List<String>> counties = jsonAdapter.fromJson(json);
    if (counties == null || counties.isEmpty()) {
      return new HashMap<>();
    }

    // Skip the header row
    counties.remove(0);

    Map<String, String> countyCodes = new HashMap<>();
    for (List<String> county : counties) {
      if (county.size() >= 3) {
        String fullCountyName = county.get(0).split(",")[0]; // Extract just the county name
        String countyCode = county.get(2);
        countyCodes.put(fullCountyName, countyCode);
      }
    }
    return countyCodes;
  }

  /**
   * Method that sends an API request to retrieve county code data given a state code
   *
   * @param String stateCode
   * @return String Json response
   */
  private static String sendRequest(String stateCode)
      throws URISyntaxException, IOException, InterruptedException {
    String requestUri =
        String.format(
            "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:%s",
            stateCode);

    HttpRequest request = HttpRequest.newBuilder().uri(new URI(requestUri)).GET().build();

    HttpResponse<String> response =
        HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());

    return response.body();
  }
}
