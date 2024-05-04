package edu.brown.cs.student.main.server.recommendations;

import com.google.cloud.firestore.*;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class RecommendationEngine {

  private final FirebaseUtilities firebaseUtilities;
  private Map<String, Map<String, Integer>> allData;
  private Map<String, Map<String, Integer>> trainingData;
  private Map<String, Map<String, Integer>> testingData;
  double[] thresholds;
  double bestThreshold;

  public RecommendationEngine(
      FirebaseUtilities firebaseUtilities, Date splitDate, double[] thresholds) {
    this.firebaseUtilities = firebaseUtilities;
    this.thresholds = thresholds;
    this.bestThreshold = 0;
    try {
      this.allData = this.firebaseUtilities.getInteractionsBySplit(splitDate).get("all");
      this.trainingData = this.firebaseUtilities.getInteractionsBySplit(splitDate).get("train");
      this.testingData = this.firebaseUtilities.getInteractionsBySplit(splitDate).get("test");
    } catch (ExecutionException | InterruptedException e) {
      // Handle exceptions here, e.g., log them or set fields to null
      this.allData = null;
      this.trainingData = null;
      this.testingData = null;
    }
  }

  public Map<String, Double> trainAndEvaluate(
      Map<String, Map<String, Integer>> trainingData,
      Map<String, Map<String, Integer>> testingData,
      double similarityThreshold)
      throws ExecutionException, InterruptedException {
    // Compute item similarities with the given threshold
    Map<String, Map<String, Double>> itemSimilarities =
        RecommendationUtils.computeItemSimilarities(trainingData, similarityThreshold);

    // Generate predictions using the computed similarities
    Map<String, List<String>> predictions = generatePredictions(testingData, itemSimilarities);

    // Evaluate the predictions
    double precision = RecommendationUtils.calculatePrecision(testingData, predictions);
    double recall = RecommendationUtils.calculateRecall(testingData, predictions);
    double f1Score = 2 * (precision * recall) / (precision + recall);

    Map<String, Double> performanceMetrics = new HashMap<>();
    performanceMetrics.put("Precision", precision);
    performanceMetrics.put("Recall", recall);
    performanceMetrics.put("F1Score", f1Score);

    return performanceMetrics;
  }

  public Map<String, List<String>> generatePredictions(
      Map<String, Map<String, Integer>> testData, Map<String, Map<String, Double>> itemSimilarities)
      throws ExecutionException, InterruptedException {
    Map<String, List<String>> predictions = new HashMap<>();

    // Iterate over each user in the test data
    for (Map.Entry<String, Map<String, Integer>> userEntry : testData.entrySet()) {
      String userId = userEntry.getKey();
      Map<String, Integer> userItems = userEntry.getValue();

      List<String> claimList;
      List<String> watchList;
      List<String> sellingList;
      try {
        // Fetch the claim list and watch list for the user
        claimList = firebaseUtilities.getClaimList(userId);
        watchList = firebaseUtilities.getWatchList(userId);
        sellingList = firebaseUtilities.getSellingList(userId);
      } catch (InterruptedException | ExecutionException e) {
        // Handle the InterruptedException, e.g., log it or rethrow
        throw e;
      }
      System.out.println("User items: " + userItems);

      // Combine both lists into a single set for quick lookup
      Set<String> excludedItems = new HashSet<>(claimList);
      excludedItems.addAll(watchList);
      excludedItems.addAll(sellingList);

      // This map will hold potential items to recommend with their cumulative similarity scores
      Map<String, Double> itemScores = new HashMap<>();

      // Iterate over each item the user has interacted with
      for (String item : userItems.keySet()) {
        // Check if this item has any similarities with other items
        if (itemSimilarities.containsKey(item)) {
          Map<String, Double> similarItems = itemSimilarities.get(item);

          // Add these similarities to the itemScores map, summing scores for items that appear
          // multiple times
          for (Map.Entry<String, Double> entry : similarItems.entrySet()) {
            String similarItem = entry.getKey();
            Double similarityScore = entry.getValue();

            // dont recommend claim list and watch list items
            if (!excludedItems.contains(similarItem)) {
              itemScores.merge(similarItem, similarityScore, Double::sum);
            }
          }
        }
      }

      // Convert the scores map to a sorted list of recommended items
      List<String> recommendedItems =
          itemScores.entrySet().stream()
              .sorted(
                  Map.Entry.<String, Double>comparingByValue()
                      .reversed()) // Sort by similarity score in descending order
              .map(Map.Entry::getKey)
              .collect(Collectors.toList());

      // Store the recommendations for this user
      predictions.put(userId, recommendedItems);
    }

    return predictions;
  }

  public double findBestThreshold() throws ExecutionException, InterruptedException {
    double bestF1Score = 0;
    double bestThreshold = 0;
    for (double threshold : this.thresholds) {
      Map<String, Double> metrics = trainAndEvaluate(trainingData, testingData, threshold);
      double currentF1Score = metrics.get("F1Score");
      if (currentF1Score > bestF1Score) {
        bestF1Score = currentF1Score;
        bestThreshold = threshold;
      }
    }
    return bestThreshold;
  }

  // Method to retrain the model using the best threshold
  public void retrainAndSave(double bestThreshold) throws ExecutionException, InterruptedException {
    Map<String, Map<String, Double>> itemSimilarities =
        RecommendationUtils.computeItemSimilarities(allData, bestThreshold);
    Map<String, List<String>> allUserRecommendations =
        generatePredictions(allData, itemSimilarities);
    System.out.println("itemSimilarities is " + itemSimilarities);
    System.out.println("allData " + allData);
    System.out.println("allUserRecommendations is " + allUserRecommendations);
    this.firebaseUtilities.saveRecommendations(allUserRecommendations);
  }
}
