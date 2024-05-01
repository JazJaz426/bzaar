package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ModifyWatchListHandler implements Route {
    final private FirebaseUtilities firebaseUtilities;

    public ModifyWatchListHandler(FirebaseUtilities firebaseUtilities) {
        this.firebaseUtilities = firebaseUtilities;
    } 

    @Override
    public Object handle(Request request, Response response) {
        String itemId = request.queryParams("itemId");
        String userId = request.queryParams("userId");
        String operation = request.queryParams("operation");
        if (itemId == null || itemId.isBlank() || userId == null || userId.isBlank() || operation == null || operation.isBlank()) {
            response.status(400);
            return Utils.toMoshiJson(Map.of("status", 400, "message", "Item ID, user ID, and operation are required"));
        }
        try {
            this.firebaseUtilities.modifyWatchList(itemId, userId, operation);
            response.status(200);
            return Utils.toMoshiJson(Map.of("status", 200, "message", "Modified watchlist successfully! itemId: " + itemId + " userId: " + userId + " operation: " + operation));
        } catch (Exception e) {
            response.status(5000);
            return Utils.toMoshiJson(Map.of("status", 500, "message", "Failed to add item to watchlist: " + e.getMessage() + " itemId: " + itemId + " userId: " + userId + " operation: " + operation));
        }



    }

}
