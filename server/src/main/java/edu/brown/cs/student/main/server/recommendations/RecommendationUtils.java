package edu.brown.cs.student.main.server.recommendations;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class providing static methods to compute similarities and process data for
 * recommendations.
 */
public class RecommendationUtils {

  /**
   * Computes the cosine similarity between two item vectors.
   *
   * @param vectorA the first vector, represented as a map of item to integer values
   * @param vectorB the second vector, represented as a map of item to integer values
   * @return the cosine similarity as a double
   */
  public static double computeCosineSimilarity(
      Map<String, Integer> vectorA, Map<String, Integer> vectorB) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    for (Map.Entry<String, Integer> entry : vectorA.entrySet()) {
      int valueA = entry.getValue();
      Integer valueB = vectorB.get(entry.getKey());
      if (valueB != null) {
        dotProduct += valueA * valueB;
      }
      normA += Math.pow(valueA, 2);
    }
    for (Integer value : vectorB.values()) {
      normB += Math.pow(value, 2);
    }
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
  }

  /**
   * Processes user interactions from a database snapshot and organizes them into a map.
   *
   * @param snapshot the QuerySnapshot from Firestore
   * @return a map where the key is a user ID and the value is another map of item IDs to
   *     interaction weights
   */
  public static Map<String, Map<String, Integer>> processInteractions(QuerySnapshot snapshot) {
    Map<String, Map<String, Integer>> userItemInteractions = new HashMap<>();
    for (DocumentSnapshot doc : snapshot.getDocuments()) {
      String userId = doc.getString("userId");
      String itemId = doc.getString("itemId");
      String interactionType = doc.getString("interactionType");
      int weight = getInteractionWeight(interactionType);

      userItemInteractions
          .computeIfAbsent(userId, k -> new HashMap<>())
          .merge(itemId, weight, Integer::sum);
    }
    return userItemInteractions;
  }

  /**
   * Converts interaction types into numerical weights.
   *
   * @param interactionType the type of interaction as a string
   * @return the weight of the interaction as an integer
   */
  public static int getInteractionWeight(String interactionType) {
    switch (interactionType) {
      case "clicked":
        return 1;
      case "liked":
        return 2;
      case "claimed":
        return 3;
      default:
        return 0;
    }
  }

  /**
   * Computes item similarities above a certain threshold.
   *
   * @param userItemMap a map of user IDs to their item interactions
   * @param similarityThreshold the minimum similarity score to consider
   * @return a map of item IDs to other item IDs and their similarity scores
   */
  public static Map<String, Map<String, Double>> computeItemSimilarities(
      Map<String, Map<String, Integer>> userItemMap, double similarityThreshold) {
    Map<String, Map<String, Double>> itemSimilarities = new HashMap<>();
    System.out.println("user item map is " + userItemMap);
    // Create item vectors
    Map<String, Map<String, Integer>> itemVectors = new HashMap<>();
    for (Map.Entry<String, Map<String, Integer>> userEntry : userItemMap.entrySet()) {
      String user = userEntry.getKey();
      Map<String, Integer> items = userEntry.getValue();
      for (Map.Entry<String, Integer> itemEntry : items.entrySet()) {
        String item = itemEntry.getKey();
        Integer weight = itemEntry.getValue();
        itemVectors.computeIfAbsent(item, k -> new HashMap<>()).put(user, weight);
      }
    }

    // Compute cosine similarities and apply the threshold
    for (String item1 : itemVectors.keySet()) {
      for (String item2 : itemVectors.keySet()) {
        if (!item1.equals(item2)) {
          double similarity =
              computeCosineSimilarity(itemVectors.get(item1), itemVectors.get(item2));
          if (similarity >= similarityThreshold) { // Apply the threshold
            itemSimilarities.computeIfAbsent(item1, k -> new HashMap<>()).put(item2, similarity);
          }
        }
      }
    }
    return itemSimilarities;
  }

  /**
   * Calculates the precision of recommendations based on test data and predictions.
   *
   * @param testData a map of user IDs to their actual interactions
   * @param predictions a map of user IDs to lists of recommended item IDs
   * @return the precision as a double
   */
  public static double calculatePrecision(
      Map<String, Map<String, Integer>> testData, Map<String, List<String>> predictions) {
    int totalRelevantItems = 0;
    int totalRecommendedItems = 0;
    int totalCorrectRecommendations = 0;

    for (Map.Entry<String, Map<String, Integer>> entry : testData.entrySet()) {
      String userId = entry.getKey();
      Map<String, Integer> userInteractions = entry.getValue();
      List<String> userRecommendations = predictions.getOrDefault(userId, new ArrayList<>());

      Set<String> relevantItems = userInteractions.keySet();
      Set<String> recommendedItems = new HashSet<>(userRecommendations);

      totalRelevantItems += relevantItems.size();
      totalRecommendedItems += recommendedItems.size();
      for (String item : recommendedItems) {
        if (relevantItems.contains(item)) {
          totalCorrectRecommendations++;
        }
      }
    }

    return totalRecommendedItems == 0
        ? 0
        : (double) totalCorrectRecommendations / totalRecommendedItems;
  }

  /**
   * Calculates the recall of recommendations based on test data and predictions.
   *
   * @param testData a map of user IDs to their actual interactions
   * @param predictions a map of user IDs to lists of recommended item IDs
   * @return the recall as a double
   */
  public static double calculateRecall(
      Map<String, Map<String, Integer>> testData, Map<String, List<String>> predictions) {
    int totalRelevantItems = 0;
    int totalCorrectRecommendations = 0;

    for (Map.Entry<String, Map<String, Integer>> entry : testData.entrySet()) {
      String userId = entry.getKey();
      Map<String, Integer> userInteractions = entry.getValue();
      List<String> userRecommendations = predictions.getOrDefault(userId, new ArrayList<>());

      Set<String> relevantItems = userInteractions.keySet();
      Set<String> recommendedItems = new HashSet<>(userRecommendations);

      totalRelevantItems += relevantItems.size();
      for (String item : recommendedItems) {
        if (relevantItems.contains(item)) {
          totalCorrectRecommendations++;
        }
      }
    }

    return totalRelevantItems == 0 ? 0 : (double) totalCorrectRecommendations / totalRelevantItems;
  }
}
