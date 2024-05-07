package edu.brown.cs.student.server.storage;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MockFirebaseUtilities implements StorageInterface {


    @Override
    public void clearUser(String uid) throws IllegalArgumentException {

    }

    @Override
    public Map<String, Object> getUserDocumentByEmail(String email)
        throws InterruptedException, ExecutionException {
        // Mock behavior to simulate database interaction
        if (email == null || email.isEmpty()) {
            return null; // Simulate user not found
        } else if (email.equals("test@example.com")) {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("email", "test@example.com");
            userProfile.put("name", "Test User");
            userProfile.put("id", "12345");
            return userProfile; // Simulate successful retrieval
        } else {
            return null; // Simulate user not found for other emails
        }
    }

    @Override
    public Map<String, Object> getUserDocumentById(String userId)
        throws InterruptedException, ExecutionException {
        // Mock behavior to simulate database interaction
        if (userId == null || userId.isEmpty()) {
            return null; // Simulate user not found
        } else if (userId.equals("12345")) {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("email", "test@example.com");
            userProfile.put("name", "Test User");
            userProfile.put("id", "12345");
            return userProfile; // Simulate successful retrieval
        } else {
            return null; // Simulate user not found for other user IDs
        }
    }

    @Override
    public Map<String, Object> getItemDetails(String itemId)
        throws InterruptedException, ExecutionException {
        if (itemId == null || itemId.isEmpty()) {
            return null;
        } else if (itemId.equals("item123")) {
            Map<String, Object> itemDetails = new HashMap<>();
            itemDetails.put("title", "Mock Item");
            itemDetails.put("description", "This is a mock item for testing.");
            itemDetails.put("price", 19.99);
            itemDetails.put("category", "Mock Category");
            return itemDetails;
        } else {
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getCollection(String uid, String collection_id)
        throws InterruptedException, ExecutionException, IllegalArgumentException {
        return null;
    }

    @Override
    public List<Map<String, Object>> getCollection(String collection_id)
        throws InterruptedException, ExecutionException, IllegalArgumentException {
        if (collection_id == null || collection_id.isEmpty()) {
            return null; // Simulate collection not found
        } else if (collection_id.equals("items")) {
            List<Map<String, Object>> items = new ArrayList<>();
            Map<String, Object> itemDetails = new HashMap<>();
            itemDetails.put("title", "Mock Item 1");
            itemDetails.put("description", "Description for Mock Item 1");
            itemDetails.put("price", 25.00);
            itemDetails.put("category", "Mock Category 1");
            items.add(itemDetails);

            itemDetails = new HashMap<>();
            itemDetails.put("title", "Mock Item 2");
            itemDetails.put("description", "Description for Mock Item 2");
            itemDetails.put("price", 15.75);
            itemDetails.put("category", "Mock Category 2");
            items.add(itemDetails);

            return items;
        } else {
            return null; // Simulate collection not found for other IDs
        }
    }

    @Override
    public List<String> getClaimList(String userId)
        throws InterruptedException, ExecutionException {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        } else if (userId.equals("12345")) {
            return List.of("claim1", "claim2", "claim3");
        } else {
            return null;
        }
    }

    @Override
    public void recordUserActivity(String interactionType, String itemId, String userId)
        throws ExecutionException, InterruptedException {

    }

    @Override
    public List<Map<String, Object>> getItemsByUser(String userId)
        throws ExecutionException, InterruptedException {
        if (userId == null || userId.isEmpty()) {
            return null;
        } else if (userId.equals("12345")) {
            List<Map<String, Object>> items = new ArrayList<>();
            Map<String, Object> itemDetails = new HashMap<>();
            itemDetails.put("title", "Mock Item 1");
            itemDetails.put("description", "Description for Mock Item 1");
            itemDetails.put("price", 25.00);
            itemDetails.put("category", "Mock Category 1");
            items.add(itemDetails);

            itemDetails = new HashMap<>();
            itemDetails.put("title", "Mock Item 2");
            itemDetails.put("description", "Description for Mock Item 2");
            itemDetails.put("price", 15.75);
            itemDetails.put("category", "Mock Category 2");
            items.add(itemDetails);

            return items;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void modifyClaimList(String itemId, String userId, String operation)
        throws ExecutionException, InterruptedException {

    }

    @Override
    public void saveRecommendations(Map<String, List<String>> allUserRecommendations)
        throws ExecutionException, InterruptedException {

    }

    @Override
    public List<String> getRecList(String userId) throws InterruptedException, ExecutionException {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        } else if (userId.equals("12345")) {
            return List.of("Item1", "Item2", "Item3");
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getSellingList(String userId)
        throws InterruptedException, ExecutionException {
        // Assuming similar logic for selling list for consistency in mock behavior
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        } else if (userId.equals("12345")) {
            return List.of("Item4", "Item5", "Item6");
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void modifyWatchList(String itemId, String userId, String operation)
        throws ExecutionException, InterruptedException {

    }

    @Override
    public List<String> getWatchList(String userId)
        throws InterruptedException, ExecutionException {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        } else if (userId.equals("12345")) {
            return List.of("WatchItem1", "WatchItem2", "WatchItem3");
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void updateItemStatus(String itemId, String status)
        throws InterruptedException, ExecutionException {

    }

    @Override
    public List<Map<String, Object>> searchItemsByKeyword(String keyword)
        throws ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public void deleteItem(String itemId, String userId)
        throws ExecutionException, InterruptedException {

    }

    @Override
    public Map<String, Map<String, Map<String, Integer>>> getInteractionsBySplit(Date splitDate)
        throws ExecutionException, InterruptedException {
        return null;
    }
}
