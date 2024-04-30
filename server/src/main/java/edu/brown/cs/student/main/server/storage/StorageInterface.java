package edu.brown.cs.student.main.server.storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StorageInterface {

  void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data);

  void removeDocument(String uid, String collectionId, Map<String, Object> query)
      throws InterruptedException, ExecutionException;

  void clearUser(String uid) throws InterruptedException, ExecutionException;

  Map<String, Object> getUserDocumentByEmail(String email)
      throws InterruptedException, ExecutionException;

  Map<String, Object> getUserDocumentById(String userId)
      throws InterruptedException, ExecutionException;

  public Map<String, Object> getItemDetails(String itemId)
      throws InterruptedException, ExecutionException;

  List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException;
}