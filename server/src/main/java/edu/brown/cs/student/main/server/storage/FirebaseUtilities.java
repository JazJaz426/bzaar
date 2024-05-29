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
import edu.brown.cs.student.main.server.recommendations.RecommendationUtils;
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

  @Override
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

  public List<String> getUniqueItemIds() throws InterruptedException, ExecutionException {
    List<Map<String, Object>> items = getCollection("items");
    List<String> uniqueItemIds = new ArrayList<>();
    for (Map<String, Object> item : items) {
      if (item.containsKey("id")) {
        uniqueItemIds.add(item.get("id").toString());
      }
    }
    return uniqueItemIds;
  }

  @Override
  public List<Map<String, Object>> getItemsByUser(String userId)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference itemsRef = db.collection("items");
    DocumentSnapshot userDoc = db.collection("users").document(userId).get().get();
    List<String> sellList = (List<String>) userDoc.get("sellList");
    List<Map<String, Object>> items = new ArrayList<>();
    for (String itemId : sellList) {
      items.add(getItemDetails(itemId));
    }
    return items;
  }

  public class FirebaseUploadHelper {

    public static String uploadFile(InputStream fileStream, String fileName, String fileType)
        throws Exception {
      String mimeType;
      switch (fileType) {
        case ".jpg":
        case ".jpeg":
          mimeType = "image/jpeg";
          break;
        case ".png":
          mimeType = "image/png";
          break;
        default:
          throw new IllegalArgumentException("Unsupported file type: " + fileType);
      }

      // Upload the file to Firebase Storage with the correct MIME type
      Blob blob =
          StorageClient.getInstance()
              .bucket("term-project-fd27e.appspot.com")
              .create(fileName, fileStream, mimeType);
      // Retrieve the media link and clean it
      URL url = new URL(blob.getMediaLink());
      System.out.println("URL: " + url);
      //      String cleanedUrl = cleanUrl(url);
      return url.toString();
      //      return cleanedUrl; // Return the cleaned URL
    }
  }

  public class FirestoreHelper {
    public static String saveItem(Item item) {
      Firestore db = FirestoreClient.getFirestore();
      DocumentReference docRef = db.collection("items").document();
      ApiFuture<WriteResult> result = docRef.set(item);
      updateSellList(item.getSeller(), docRef.getId());
      System.out.println("Item saved with ID: " + docRef.getId());
      return docRef.getId();
    }

    public static void updateSellList(String userId, String itemId) {
      Firestore db = FirestoreClient.getFirestore();
      DocumentReference userRef = db.collection("users").document(userId);
      userRef.update("sellList", FieldValue.arrayUnion(itemId));
      System.out.println("Sell list updated for user: " + userId);
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
      Map<String, Object> item = document.getData();
      item.put("id", document.getId());
      return item;
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
        updateItemStatus(itemId, userId, "claimed");
      } else {
        System.out.println("Item with ID " + itemId + " does not exist.");
      }
    } else if ("del".equalsIgnoreCase(operation)) {
      future = userRef.update("claimList", FieldValue.arrayRemove(itemId));
      future.get();
      updateItemStatus(itemId, userId, "available");
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public void updateItemStatus(String itemId, String claimerId, String status)
      throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference itemRef = db.collection("items").document(itemId);
    DocumentSnapshot itemSnapshot = itemRef.get().get();
    if (itemSnapshot.exists()) {
      try {
        ApiFuture<WriteResult> writeResult;
        if (status == "claimed") {
          writeResult =
              itemRef.update(
                  "status", status,
                  "claimerId", claimerId);
        } else {
          writeResult = itemRef.update("status", status, "claimerId", null);
        }
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
  @Override
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

  @Override
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

  @Override
  public Map<String, Map<String, Map<String, Integer>>> getInteractionsBySplit(Date splitDate)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference interactionsRef = db.collection("interactions");

    // Fetch all data
    ApiFuture<QuerySnapshot> allDataFuture = interactionsRef.get();
    Map<String, Map<String, Integer>> allData =
        RecommendationUtils.processInteractions(allDataFuture.get());
    // Fetch training data
    ApiFuture<QuerySnapshot> trainingSetFuture =
        interactionsRef.whereLessThan("timestamp", splitDate).get();
    Map<String, Map<String, Integer>> trainingData =
        RecommendationUtils.processInteractions(trainingSetFuture.get());

    // Fetch testing data
    ApiFuture<QuerySnapshot> testingSetFuture =
        interactionsRef.whereGreaterThanOrEqualTo("timestamp", splitDate).get();
    Map<String, Map<String, Integer>> testingData =
        RecommendationUtils.processInteractions(testingSetFuture.get());

    // Organize data into a map
    Map<String, Map<String, Map<String, Integer>>> result = new HashMap<>();
    result.put("all", allData);
    result.put("train", trainingData);
    result.put("test", testingData);
    return result;
  }

  public void saveRecommendations(Map<String, List<String>> allUserRecommendations)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    for (Map.Entry<String, List<String>> entry : allUserRecommendations.entrySet()) {
      String userId = entry.getKey();
      List<String> recommendations = entry.getValue();
      DocumentReference userRef = db.collection("users").document(userId);
      userRef.update("recommendations", recommendations);
    }
  }

  public List<String> getRecList(String userId) throws InterruptedException, ExecutionException {
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
      List<String> recList = (List<String>) document.get("recommendations");
      if (recList != null) {
        List<String> validItems = new ArrayList<>();
        for (String itemId : recList) {
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

  public List<String> getSellingList(String userId)
      throws InterruptedException, ExecutionException {
    List<String> itemIds = new ArrayList<>();
    try {
      List<Map<String, Object>> items = getItemsByUser(userId);
      Firestore db = FirestoreClient.getFirestore();
      DocumentSnapshot userDoc = db.collection("users").document(userId).get().get();
      List<String> sellList = (List<String>) userDoc.get("sellList");
      for (Map<String, Object> item : items) {
        if (item.containsKey("id")) {
          String itemId = item.get("id").toString();
          itemIds.add(itemId);
          if (sellList == null || !sellList.contains(itemId)) {
            if (sellList == null) {
              sellList = new ArrayList<>();
            }
            sellList.add(itemId);
            // Update Firestore user document with new sellList
            db.collection("users").document(userId).update("sellList", sellList);
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Failed to get items by user: " + e.getMessage());
    }
    return itemIds;
  }

  public void updateUserProfile(String userEmail, String name, String address)
      throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    ApiFuture<QuerySnapshot> future = db.collection("users").whereEqualTo("email", userEmail).get();
    String userId = "";
    for (DocumentSnapshot document : future.get().getDocuments()) {
      userId = document.getId();
    }
    DocumentReference docRef = db.collection("users").document(userId);
    Map<String, Object> updates = new HashMap<>();
    updates.put("name", name);
    updates.put("address", address);
    docRef.update(updates);
  }
}
