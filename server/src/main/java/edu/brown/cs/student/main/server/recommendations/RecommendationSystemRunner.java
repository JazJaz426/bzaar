package edu.brown.cs.student.main.server.recommendations;

import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.Calendar;
import java.util.Date;

/**
 * The RecommendationSystemRunner class is responsible for initializing and running the
 * recommendation system. It sets up the necessary components and handles any exceptions that may
 * occur during the process.
 *
 * @author [author name]
 * @version 1.0
 */
public class RecommendationSystemRunner {
  /**
   * The main method that serves as the entry point for the recommendation system. It initializes
   * necessary utilities, sets up data, and triggers the recommendation process.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    try {
      // Initialize FirebaseUtilities to interact with Firebase services
      FirebaseUtilities firebaseUtilities = new FirebaseUtilities();

      // Establish the date used to split the data into training and testing sets
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DATE, -1);
      Date splitDate = calendar.getTime(); // 2024-05-06

      // Define thresholds for similarity scoring in the recommendation engine
      double[] thresholds = new double[] {0.1, 0.2, 0.3, 0.4, 0.5};

      // Instantiate the RecommendationEngine with the initialized utilities and parameters
      RecommendationEngine recommendationEngine =
          new RecommendationEngine(firebaseUtilities, splitDate, thresholds);

      // Determine the best threshold for item similarity that maximizes the F1 score
      double bestThreshold = recommendationEngine.findBestThreshold();
      // Retrain the recommendation model with the best threshold and save the results
      recommendationEngine.retrainAndSave(bestThreshold);

      // Notify successful saving of recommendations
      System.out.println("Recommendations saved successfully.");
    } catch (Exception e) {
      // Log any exceptions that occur during the recommendation process
      System.err.println("Error during recommendation process: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
