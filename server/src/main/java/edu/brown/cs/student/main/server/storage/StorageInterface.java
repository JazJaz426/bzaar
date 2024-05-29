package edu.brown.cs.student.main.server.storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StorageInterface {

  void clearUser(String uid) throws IllegalArgumentException;

  Map<String, Object> getUserDocumentByEmail(String email)
      throws InterruptedException, ExecutionException;

  Map<String, Object> getUserDocumentById(String userId)
      throws InterruptedException, ExecutionException;

  Map<String, Object> getItemDetails(String itemId) throws InterruptedException, ExecutionException;

  List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException;

  List<Map<String, Object>> getCollection(String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException;

  List<String> getClaimList(String userId) throws InterruptedException, ExecutionException;

  void recordUserActivity(String interactionType, String itemId, String userId)
      throws ExecutionException, InterruptedException;

  List<Map<String, Object>> getItemsByUser(String userId)
      throws ExecutionException, InterruptedException;

  void modifyClaimList(String itemId, String userId, String operation)
      throws ExecutionException, InterruptedException;

  void saveRecommendations(Map<String, List<String>> allUserRecommendations)
      throws ExecutionException, InterruptedException;

  List<String> getRecList(String userId) throws InterruptedException, ExecutionException;

  List<String> getSellingList(String userId) throws InterruptedException, ExecutionException;

  void modifyWatchList(String itemId, String userId, String operation)
      throws ExecutionException, InterruptedException;

  List<String> getWatchList(String userId) throws InterruptedException, ExecutionException;

  void updateItemStatus(String itemId, String claimerId, String status)
      throws InterruptedException, ExecutionException;

  List<Map<String, Object>> searchItemsByKeyword(String keyword)
      throws ExecutionException, InterruptedException;

  void deleteItem(String itemId, String userId) throws ExecutionException, InterruptedException;

  Map<String, Map<String, Map<String, Integer>>> getInteractionsBySplit(java.util.Date splitDate)
      throws ExecutionException, InterruptedException;

  public void updateUserProfile(String userEmail, String name, String address)
      throws InterruptedException, ExecutionException;
}
