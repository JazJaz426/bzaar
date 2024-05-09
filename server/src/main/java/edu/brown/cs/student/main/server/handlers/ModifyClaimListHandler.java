package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ModifyClaimListHandler implements Route {
  private final StorageInterface firebaseUtilities;

  public ModifyClaimListHandler(StorageInterface firebaseUtilities) {
    this.firebaseUtilities = firebaseUtilities;
  }

  @Override
  public Object handle(Request request, Response response) {
    String itemId = request.queryParams("itemId");
    String userId = request.queryParams("userId");
    String operation = request.queryParams("operation");
    if (itemId == null
        || itemId.isBlank()
        || userId == null
        || userId.isBlank()
        || operation == null
        || operation.isBlank()) {
      response.status(400);
      return Utils.toMoshiJson(
          Map.of("status", 400, "message", "Item ID, user ID, and operation are required"));
    }
    try {
      this.firebaseUtilities.modifyClaimList(itemId, userId, operation);
      response.status(200);
      return Utils.toMoshiJson(
          Map.of(
              "status",
              200,
              "message",
              "Modified claimlist successfully! itemId: "
                  + itemId
                  + " userId: "
                  + userId
                  + " operation: "
                  + operation));
    } catch (Exception e) {
      response.status(5000);
      return Utils.toMoshiJson(
          Map.of(
              "status",
              500,
              "message",
              "Failed to add item to claimlist: "
                  + e.getMessage()
                  + " itemId: "
                  + itemId
                  + " userId: "
                  + userId
                  + " operation: "
                  + operation));
    }
  }
}
