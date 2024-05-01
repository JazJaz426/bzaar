package edu.brown.cs.student.main.server.storage;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

  public void recordUserActivity(String interactionType, String itemId, String userId) {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference collectionRef = db.collection("interactions");
    Map<String, Object> data = new HashMap<>();
    data.put("interactionType", interactionType);
    data.put("itemId", itemId);
    data.put("userId", userId);
    collectionRef.document().set(data);


  }


}
