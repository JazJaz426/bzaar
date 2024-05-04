package edu.brown.cs.student.main.server.recommendations;

import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.Date;

public class RecommendationSystemRunner {
  public static void main(String[] args) {
    try {
      // Initialize FirebaseUtilities
      FirebaseUtilities firebaseUtilities = new FirebaseUtilities();

      Date splitDate = new Date();
      double[] thresholds = new double[] {0.0, 0.1, 0.2, 0.3, 0.4, 0.5};

      // Create an instance of RecommendationEngine
      RecommendationEngine recommendationEngine =
          new RecommendationEngine(firebaseUtilities, splitDate, thresholds);

      double bestThreshold = recommendationEngine.findBestThreshold();
      recommendationEngine.retrainAndSave(bestThreshold);

      System.out.println("Recommendations saved successfully.");
    } catch (Exception e) {
      System.err.println("Error during recommendation process: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
