package edu.brown.cs.student.main.server.storage;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.Blob;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseUtilities implements StorageInterface {

  public FirebaseUtilities() throws IOException {
    String workingDirectory = System.getProperty("user.dir");
    Path firebaseConfigPath =
        Paths.get(workingDirectory, "src", "main", "resources", "firebase_config.json");
    // ^-- if your /resources/firebase_config.json exists but is not found,
    // try printing workingDirectory and messing around with this path.

    FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toString());

    FirebaseOptions options =
        new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setStorageBucket("term-project-fd27e.appspot.com")
            .build();

    FirebaseApp.initializeApp(options);
  }

  @Override
  public void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data)
      throws IllegalArgumentException {
    if (uid == null || collection_id == null || doc_id == null || data == null) {
      throw new IllegalArgumentException(
          "addDocument: uid, collection_id, doc_id, or data cannot be null");
    }
    // adds a new document 'doc_name' to colleciton 'collection_id' for user 'uid'
    // with data payload 'data'.

    // TODO: FIRESTORE PART 2:
    // use the guide below to implement this handler
    // - https://firebase.google.com/docs/firestore/quickstart#add_data

    Firestore db = FirestoreClient.getFirestore();
    // 1: Get a ref to the collection that you created
    CollectionReference docRef = db.collection("users").document(uid).collection(collection_id);
    // 2: Write data to the collection ref
    docRef.document(doc_id).set(data);
  }

  /**
   * Inverse of addDocument, used for removing specific entries rather than whole pins collection
   *
   * <p>Note: used stackOverflow + LLM to develop this component
   */
  @Override
  public void removeDocument(String uid, String collectionId, Map<String, Object> query)
      throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference collectionRef =
        db.collection("users").document(uid).collection(collectionId);

    // Find entries that match given lat/long
    com.google.cloud.firestore.Query dbQuery =
        collectionRef
            .whereEqualTo("latitude", query.get("latitude"))
            .whereEqualTo("longitude", query.get("longitude"));
    ApiFuture<QuerySnapshot> querySnapshot = dbQuery.get();

    // Given query, remove entries that match (e.g. remove specific pins)
    for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
      document.getReference().delete();
    }
  }

  /** Gather all pins for a given usba ser */
  @Override
  public List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException {
    if (uid == null || collection_id == null) {
      throw new IllegalArgumentException("getCollection: uid and/or collection_id cannot be null");
    }

    // gets all documents in the collection 'collection_id' for user 'uid'

    Firestore db = FirestoreClient.getFirestore();
    // 1: Make the data payload to add to your collection
    CollectionReference dataRef = db.collection("users").document(uid).collection(collection_id);
    // 2: Get pin documents
    QuerySnapshot dataQuery = dataRef.get().get();

    // 3: Get data from document queries
    List<Map<String, Object>> data = new ArrayList<>();
    for (QueryDocumentSnapshot doc : dataQuery.getDocuments()) {
      data.add(doc.getData());
    }

    return data;
  }

  public List<Map<String, Object>> getCollection(String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException {
    if (collection_id == null) {
      throw new IllegalArgumentException("getCollection: collection_id cannot be null");
    }
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference dataRef = db.collection(collection_id);
    QuerySnapshot dataQuery = dataRef.get().get();
    List<Map<String, Object>> data = new ArrayList<>();

    for (QueryDocumentSnapshot doc : dataQuery.getDocuments()) {
      Map<String, Object> item = doc.getData();
      item.put("id", doc.getId());
      data.add(item);
    }

    return data;
  }

  public List<Map<String, Object>> getItemsByUser(String userId)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference itemsRef = db.collection("items");
    com.google.cloud.firestore.Query query = itemsRef.whereEqualTo("seller", userId);
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<Map<String, Object>> items = new ArrayList<>();
    for (QueryDocumentSnapshot doc : querySnapshot.get().getDocuments()) {
      Map<String, Object> item = doc.getData();
      item.put("id", doc.getId());
      items.add(item);
    }
    return items;
  }

  public class FirebaseUploadHelper {

    public static String uploadFile(InputStream fileStream, String fileName) throws Exception {
      Blob blob =
          StorageClient.getInstance()
              .bucket("term-project-fd27e.appspot.com")
              .create(fileName, fileStream, "image/jpeg");

      // Retrieve the media link and clean it
      URL url = new URL(blob.getMediaLink());
      System.out.println("URL: " + url);
      //      String cleanedUrl = cleanUrl(url);
      return url.toString();
      //      return cleanedUrl; // Return the cleaned URL
    }

    // A method to clean or modify the URL as needed
    private static String cleanUrl(URL url) {
      String path = url.getPath();
      path = path.replace("/download", ""); // Remove download if needed

      // Remove query parameters or modify as needed
      String cleanPath = path.split("\\?")[0];

      return url.getProtocol() + "://" + url.getHost() + cleanPath;
    }
  }

  public class FirestoreHelper {
    public static void saveItem(Item item) {
      System.out.println("Saving item to Firestore");
      Firestore db = FirestoreClient.getFirestore();
      db.collection("items").document().set(item);
    }
  }

  @Override
  public Map<String, Object> getUserDocumentByEmail(String email)
      throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference usersRef = db.collection("users");
    com.google.cloud.firestore.Query query = usersRef.whereEqualTo("email", email);
    ApiFuture<QuerySnapshot> querySnapshot = query.get();

    for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
      Map<String, Object> userData = document.getData();
      userData.put("userId", document.getId()); // Add the document ID as "userId"
      return userData; // Returns the first matching document's data with the document ID included
      // (should just be one document as email is unique)
    }
    return null; // Return null if no document found
  }

  @Override
  public Map<String, Object> getUserDocumentById(String userId)
      throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("users").document(userId);
    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot document = future.get();
    if (document.exists()) {
      return document.getData();
    } else {
      return null;
    }
  }

  // clears the collections inside of a specific user.

  @Override
  public void clearUser(String uid) throws IllegalArgumentException {
    if (uid == null) {
      throw new IllegalArgumentException("removeUser: uid cannot be null");
    }
    try {
      // removes all data for user 'uid'
      Firestore db = FirestoreClient.getFirestore();
      // 1: Get a ref to the user document
      DocumentReference userDoc = db.collection("users").document(uid);
      // 2: Delete the user document
      deleteDocument(userDoc);
    } catch (Exception e) {
      System.err.println("Error removing user : " + uid);
      System.err.println(e.getMessage());
    }
  }

  private void deleteDocument(DocumentReference doc) {
    // for each subcollection, run deleteCollection()
    Iterable<CollectionReference> collections = doc.listCollections();
    for (CollectionReference collection : collections) {
      deleteCollection(collection);
    }
    // then delete the document
    doc.delete();
  }

  // recursively removes all the documents and collections inside a collection
  // https://firebase.google.com/docs/firestore/manage-data/delete-data#collections
  private void deleteCollection(CollectionReference collection) {
    try {

      // get all documents in the collection
      ApiFuture<QuerySnapshot> future = collection.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      // delete each document
      for (QueryDocumentSnapshot doc : documents) {
        doc.getReference().delete();
      }

      // NOTE: the query to documents may be arbitrarily large. A more robust
      // solution would involve batching the collection.get() call.
    } catch (Exception e) {
      System.err.println("Error deleting collection : " + e.getMessage());
    }
  }

  /**
   * Retrieves the details of a goods item by its item ID.
   *
   * @param itemId The ID of the goods item to retrieve.
   * @return A map containing the goods details, or null if no such item exists.
   * @throws InterruptedException If the thread is interrupted while waiting.
   * @throws ExecutionException If an exception is thrown during the execution.
   */
  public Map<String, Object> getItemDetails(String itemId)
      throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef =
        db.collection("items").document(itemId); // Assuming 'items' is the collection name
    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot document = future.get();
    if (document.exists()) {
      return document.getData();
    } else {
      return null;
    }
  }

  public void recordUserActivity(String interactionType, String itemId, String userId)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference collectionRef = db.collection("interactions");
    Map<String, Object> data = new HashMap<>();
    data.put("interactionType", interactionType);
    data.put("itemId", itemId);
    data.put("userId", userId);
    data.put("timestamp", new Date());
    collectionRef.document().set(data);
  }

  /**
   * Modifies a user's claim list by either adding or removing an item.
   *
   * @param itemId The ID of the item to modify in the claim list.
   * @param userId The ID of the user whose claim list is being modified.
   * @param operation Specifies whether to add or remove the item ("add" or "remove").
   * @throws ExecutionException If an exception is thrown during the execution.
   * @throws InterruptedException If the thread is interrupted while waiting.
   */
  public void modifyClaimList(String itemId, String userId, String operation)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference userCheckRef = db.collection("users").document(userId);
    ApiFuture<DocumentSnapshot> userCheckFuture = userCheckRef.get();
    DocumentSnapshot userCheckSnapshot = userCheckFuture.get();
    if (!userCheckSnapshot.exists()) {
      throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
    }
    DocumentReference userRef = db.collection("users").document(userId);
    ApiFuture<WriteResult> future;
    if ("add".equalsIgnoreCase(operation)) {
      DocumentReference itemRef = db.collection("items").document(itemId);
      DocumentSnapshot itemSnapshot = itemRef.get().get();
      if (itemSnapshot.exists()) {
        future = userRef.update("claimList", FieldValue.arrayUnion(itemId));
        future.get();
        updateItemStatus(itemId, "claimed");
      } else {
        System.out.println("Item with ID " + itemId + " does not exist.");
      }
    } else if ("del".equalsIgnoreCase(operation)) {
      future = userRef.update("claimList", FieldValue.arrayRemove(itemId));
      future.get();
      updateItemStatus(itemId, "available");
    } else {
      throw new IllegalArgumentException(
          "Invalid operation: " + operation + ". Use 'add' or 'del'.");
    }
  }

  /**
   * Fetches the list of claimed item IDs associated with a specific user.
   *
   * <p>This method retrieves the claim list from the database. If the user does not exist or the
   * claim list is empty, it returns an empty list. It also ensures that all item IDs in the claim
   * list are valid and currently exist in the database. If an item ID in the claim list does not
   * exist, it is removed from the user's claim list in the database.
   *
   * @param userId The unique identifier of the user whose claim list is being retrieved.
   * @return A List containing the item IDs in the user's claim list. Returns an empty list if the
   *     user or the claim list does not exist.
   * @throws ExecutionException If an error occurs during the database read operation.
   * @throws InterruptedException If the operation is interrupted during execution.
   */
  public List<String> getClaimList(String userId) throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference userCheckRef = db.collection("users").document(userId);
    ApiFuture<DocumentSnapshot> userCheckFuture = userCheckRef.get();
    DocumentSnapshot userCheckSnapshot = userCheckFuture.get();
    if (!userCheckSnapshot.exists()) {
      throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
    }
    DocumentReference userRef = db.collection("users").document(userId);
    ApiFuture<DocumentSnapshot> future = userRef.get();
    DocumentSnapshot document = future.get();
    if (document.exists()) {
      List<String> claimList = (List<String>) document.get("claimList");
      if (claimList != null) {
        List<String> validItems = new ArrayList<>();
        for (String itemId : claimList) {
          DocumentReference itemRef = db.collection("items").document(itemId);
          DocumentSnapshot itemDoc = itemRef.get().get();
          if (itemDoc.exists()) {
            validItems.add(itemId);
          } else {
            userRef.update("claimList", FieldValue.arrayRemove(itemId));
          }
        }
        return validItems;
      } else {
        return new ArrayList<>();
      }
    } else {
      return new ArrayList<>();
    }
  }

  /**
   * Modifies a user's watchlist by either adding or removing an item, depending on the specified
   * operation. The method first verifies the existence of the item in the database. If the
   * operation is 'add' and the item does not exist, the method terminates without altering the
   * watchlist.
   *
   * @param itemId The ID of the item to be added or removed.
   * @param userId The ID of the user whose watchlist is to be modified.
   * @param operation Specifies the type of modification ('add' to include the item, 'del' to
   *     exclude the item).
   * @throws ExecutionException If a failure occurs during the database access.
   * @throws InterruptedException If the thread running the operation is interrupted.
   */
  public void modifyWatchList(String itemId, String userId, String operation)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference userCheckRef = db.collection("users").document(userId);
    ApiFuture<DocumentSnapshot> userCheckFuture = userCheckRef.get();
    DocumentSnapshot userCheckSnapshot = userCheckFuture.get();
    if (!userCheckSnapshot.exists()) {
      throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
    }
    DocumentReference userRef = db.collection("users").document(userId);
    ApiFuture<WriteResult> future;
    if ("add".equalsIgnoreCase(operation)) {
      DocumentReference itemRef = db.collection("items").document(itemId);
      DocumentSnapshot itemDoc = itemRef.get().get();
      if (!itemDoc.exists()) {
        System.out.println("Item with ID " + itemId + " does not exist.");
        return; // Exit if the item does not exist and the operation is 'add'
      } else {
        recordUserActivity("liked", itemId, userId);
        future = userRef.update("watchList", FieldValue.arrayUnion(itemId));
      }
    } else if ("del".equalsIgnoreCase(operation)) {
      future = userRef.update("watchList", FieldValue.arrayRemove(itemId));
    } else {
      throw new IllegalArgumentException(
          "Invalid operation: " + operation + ". Use 'add' or 'del'.");
    }
    future.get(); // Ensure the operation completes
  }

  /**
   * Fetches the watchlist of a specified user using their unique user ID. This method checks the
   * existence of the user and their watchlist, and ensures all items in the watchlist still exist.
   * Items that no longer exist are removed from the watchlist.
   *
   * @param userId The unique identifier of the user whose watchlist is being retrieved.
   * @return A List containing the item IDs currently in the user's watchlist. Returns an empty list
   *     if the watchlist is empty or the user does not exist.
   * @throws ExecutionException If an error occurs during the database operation.
   * @throws InterruptedException If the operation is interrupted.
   */
  public List<String> getWatchList(String userId) throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference userCheckRef = db.collection("users").document(userId);
    ApiFuture<DocumentSnapshot> userCheckFuture = userCheckRef.get();
    DocumentSnapshot userCheckSnapshot = userCheckFuture.get();
    if (!userCheckSnapshot.exists()) {
      throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
    }
    DocumentReference userRef = db.collection("users").document(userId);
    ApiFuture<DocumentSnapshot> future = userRef.get();
    DocumentSnapshot document = future.get();
    if (document.exists()) {
      List<String> watchList = (List<String>) document.get("watchList");
      if (watchList != null) {
        List<String> validItems = new ArrayList<>();
        for (String itemId : watchList) {
          DocumentReference itemRef = db.collection("items").document(itemId);
          DocumentSnapshot itemDoc = itemRef.get().get();
          if (itemDoc.exists()) {
            validItems.add(itemId);
          } else {
            userRef.update("watchList", FieldValue.arrayRemove(itemId));
          }
        }
        return validItems;
      } else {
        return new ArrayList<>();
      }
    } else {
      return new ArrayList<>();
    }
  }

  /**
   * Updates the status of an item in the database.
   *
   * @param itemId The unique identifier of the item to update.
   * @param status The new status to set for the item.
   * @throws InterruptedException If the thread is interrupted while waiting for the database
   *     operation to complete.
   * @throws ExecutionException If an error occurs during the database operation.
   */
  public void updateItemStatus(String itemId, String status)
      throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference itemRef = db.collection("items").document(itemId);
    DocumentSnapshot itemSnapshot = itemRef.get().get();
    if (itemSnapshot.exists()) {
      try {
        ApiFuture<WriteResult> writeResult = itemRef.update("status", status);
        // Wait for the future to complete and get the result
        WriteResult result = writeResult.get();
        // Optionally, log the update time for confirmation
        System.out.println("Item updated successfully at: " + result.getUpdateTime());
      } catch (InterruptedException | ExecutionException e) {
        System.err.println("Failed to update item: " + e.getMessage());
        throw e; // Rethrow the exception to handle it further up the call stack if necessary
      }
    } else {
      System.out.println("Item with ID " + itemId + " does not exist.");
    }
  }

  /**
   * Searches for items by a keyword in their title.
   *
   * @param keyword The keyword to search for in item titles.
   * @return A list of maps, each representing an item that contains the keyword in its title.
   * @throws ExecutionException If an error occurs during the database operation.
   * @throws InterruptedException If the operation is interrupted.
   */
  public List<Map<String, Object>> searchItemsByKeyword(String keyword)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    List<Map<String, Object>> Allitems = this.getCollection("items");
    List<Map<String, Object>> items = new ArrayList<>();
    for (Map<String, Object> item : Allitems) {
      if (item.get("title").toString().toLowerCase().contains(keyword.toLowerCase())) {
        items.add(item);
      }
    }
    return items;
  }

  public void deleteItem(String itemId, String userId)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference itemRef = db.collection("items").document(itemId);
    itemRef.delete();
    // update watch list
    DocumentReference userRef = db.collection("users").document(userId);
    ApiFuture<DocumentSnapshot> future = userRef.get();
    DocumentSnapshot itemDoc = itemRef.get().get();
    userRef.update("watchList", FieldValue.arrayRemove(itemId));
    // update sell list
    userRef.update("sellList", FieldValue.arrayRemove(itemId));
  }

  // // recommendation page related utilities
  // public Map<String, Map<String, Integer>> fetchUserItemInteractions()
  //     throws ExecutionException, InterruptedException {
  //   Firestore db = FirestoreClient.getFirestore();
  //   ApiFuture<QuerySnapshot> future = db.collection("interactions").get();
  //   List<QueryDocumentSnapshot> documents = future.get().getDocuments();
  //   Map<String, Map<String, Integer>> userItemMap = new HashMap<>();

  //   for (QueryDocumentSnapshot document : documents) {
  //     String userId = document.getString("userId");
  //     String itemId = document.getString("itemId");
  //     String interactionType = document.getString("interactionType");
  //     int weight = getInteractionWeight(interactionType);

  //     userItemMap.computeIfAbsent(userId, k -> new HashMap<>()).merge(itemId, weight,
  // Integer::sum);
  //   }
  //   return userItemMap;
  // }

  // public int getInteractionWeight(String interactionType) {
  //   switch (interactionType) {
  //     case "clicked":
  //       return 1;
  //     case "liked":
  //       return 2;
  //     case "claimed":
  //       return 3;
  //     default:
  //       return 0;
  //   }
  // }

  // public void computeItemSimilarities(Map<String, Map<String, Integer>> userItemMap)
  //     throws ExecutionException, InterruptedException {
  //   Map<String, Map<String, Double>> itemSimilarities = new HashMap<>();

  //   // Create item vectors
  //   Map<String, Map<String, Integer>> itemVectors = new HashMap<>();
  //   for (String user : userItemMap.keySet()) {
  //     for (Map.Entry<String, Integer> entry : userItemMap.get(user).entrySet()) {
  //       String item = entry.getKey();
  //       Integer weight = entry.getValue();
  //       itemVectors.computeIfAbsent(item, k -> new HashMap<>()).put(user, weight);
  //     }
  //   }

  //   // Compute cosine similarities
  //   for (String item1 : itemVectors.keySet()) {
  //     for (String item2 : itemVectors.keySet()) {
  //       if (!item1.equals(item2)) {
  //         double similarity =
  //             computeCosineSimilarity(itemVectors.get(item1), itemVectors.get(item2));
  //         itemSimilarities.computeIfAbsent(item1, k -> new HashMap<>()).put(item2, similarity);
  //       }
  //     }
  //   }

  //   // Store the similarities in Firestore
  //   Firestore db = FirestoreClient.getFirestore();
  //   for (String item1 : itemSimilarities.keySet()) {
  //     DocumentReference docRef = db.collection("itemSimilarities").document(item1);
  //     docRef.set(itemSimilarities.get(item1));
  //   }
  // }

  // public double computeCosineSimilarity(
  //     Map<String, Integer> vectorA, Map<String, Integer> vectorB) {
  //   double dotProduct = 0.0;
  //   double normA = 0.0;
  //   double normB = 0.0;
  //   for (String key : vectorA.keySet()) {
  //     dotProduct += vectorA.get(key) * vectorB.getOrDefault(key, 0);
  //     normA += Math.pow(vectorA.get(key), 2);
  //   }
  //   for (Integer value : vectorB.values()) {
  //     normB += Math.pow(value, 2);
  //   }
  //   return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
  // }

  // Recommendation related code

  public void performTrainTestSplit(Date splitDate)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference interactionsRef = db.collection("interactions");
    ApiFuture<QuerySnapshot> trainingSetFuture =
        interactionsRef.whereLessThan("timestamp", splitDate).get();
    ApiFuture<QuerySnapshot> testingSetFuture =
        interactionsRef.whereGreaterThanOrEqualTo("timestamp", splitDate).get();

    // Process these futures to handle training and testing data
    Map<String, Map<String, Integer>> trainingData = processInteractions(trainingSetFuture.get());
    Map<String, Map<String, Integer>> testingData = processInteractions(testingSetFuture.get());

    // Now use trainingData to train the model
    computeItemSimilarities(trainingData);

    // Use testingData to evaluate the model
    evaluateModel(testingData);
  }

  // {userId, {itemId, weight}}
  private Map<String, Map<String, Integer>> processInteractions(QuerySnapshot snapshot) {
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

  // convert
  public int getInteractionWeight(String interactionType) {
    switch (interactionType) {
      case "clicked":
        return 1;
      case "liked":
        return 2;
      case "purchased":
        return 3;
      default:
        return 0;
    }
  }

  public void computeItemSimilarities(Map<String, Map<String, Integer>> userItemMap) {
    Map<String, Map<String, Double>> itemSimilarities = new HashMap<>();

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

    // Compute cosine similarities
    for (String item1 : itemVectors.keySet()) {
      for (String item2 : itemVectors.keySet()) {
        if (!item1.equals(item2)) {
          double similarity =
              computeCosineSimilarity(itemVectors.get(item1), itemVectors.get(item2));
          itemSimilarities.computeIfAbsent(item1, k -> new HashMap<>()).put(item2, similarity);
        }
      }
    }
  }

  public double computeCosineSimilarity(
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

  public void evaluateModel(Map<String, Map<String, Integer>> testData) {
    // Assume recommendations are fetched and processed
    Map<String, List<String>> recommendations =
        getRecommendationsForAllUsers(); // This should be adapted to fetch based on testData

    double precision = calculatePrecision(testData, recommendations);
    double recall = calculateRecall(testData, recommendations);
    double f1Score = 2 * (precision * recall) / (precision + recall);

    System.out.println("Precision: " + precision);
    System.out.println("Recall: " + recall);
    System.out.println("F1 Score: " + f1Score);
  }

  private double calculatePrecision(
      Map<String, Map<String, Integer>> testData, Map<String, List<String>> recommendations) {
    int totalRelevantItems = 0;
    int totalRecommendedItems = 0;
    int totalCorrectRecommendations = 0;

    for (Map.Entry<String, Map<String, Integer>> entry : testData.entrySet()) {
      String userId = entry.getKey();
      Map<String, Integer> userInteractions = entry.getValue();
      List<String> userRecommendations = recommendations.getOrDefault(userId, new ArrayList<>());

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

    double precision =
        totalRecommendedItems == 0
            ? 0
            : (double) totalCorrectRecommendations / totalRecommendedItems;
    return precision;
  }

  public double calculateRecall(
      Map<String, Map<String, Integer>> testData, Map<String, List<String>> recommendations) {
    int totalRelevantItems = 0;
    int totalCorrectRecommendations = 0;

    for (Map.Entry<String, Map<String, Integer>> entry : testData.entrySet()) {
      String userId = entry.getKey();
      Map<String, Integer> userInteractions = entry.getValue();
      List<String> userRecommendations = recommendations.getOrDefault(userId, new ArrayList<>());

      Set<String> relevantItems = userInteractions.keySet();
      Set<String> recommendedItems = new HashSet<>(userRecommendations);

      totalRelevantItems += relevantItems.size();
      for (String item : recommendedItems) {
        if (relevantItems.contains(item)) {
          totalCorrectRecommendations++;
        }
      }
    }

    double recall =
        totalRelevantItems == 0 ? 0 : (double) totalCorrectRecommendations / totalRelevantItems;
    return recall;
  }

  public void saveRecommendations(String userId, List<String> recommendedItemIds)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("recommendations").document(userId);
    Map<String, Object> data = new HashMap<>();
    data.put("recommendedItems", recommendedItemIds);
    docRef.set(data);
  }

  public Map<String, List<String>> getRecommendationsForAllUsers()
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    ApiFuture<QuerySnapshot> future = db.collection("recommendations").get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    Map<String, List<String>> allRecommendations = new HashMap<>();
    for (QueryDocumentSnapshot document : documents) {
      String userId = document.getId();
      List<String> recommendedItems = (List<String>) document.get("recommendedItems");
      allRecommendations.put(userId, recommendedItems);
    }
    return allRecommendations;
  }
}
