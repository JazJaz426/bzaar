package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities.FirebaseUploadHelper;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities.FirestoreHelper;
import edu.brown.cs.student.main.server.storage.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import spark.Request;
import spark.Response;
import spark.Route;

public class PostItemHandler implements Route {

  private FirebaseUtilities firebaseUtilities;

  public PostItemHandler(FirebaseUtilities firebaseUtilities) {
    this.firebaseUtilities = firebaseUtilities;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
    try {
      List<FileItem> formItems = upload.parseRequest(request.raw());
      Item item = new Item(); // Assuming Item class exists
      List<String> images = new ArrayList<>();

      for (FileItem field : formItems) {
        if (!field.isFormField()) {
          String fileName = field.getName();
          String fileExtension = fileName.substring(fileName.lastIndexOf("."));
          // Allow only JPEG and PNG file types
          if (!fileExtension.equals(".jpg")
              && !fileExtension.equals(".jpeg")
              && !fileExtension.equals(".png")) {
            responseMap.put("status", 415); // Using HTTP 415 Unsupported Media Type
            responseMap.put(
                "message", "Unsupported file type. Please upload only JPG, JPEG, or PNG images.");
            return Utils.toMoshiJson(responseMap);
          }
          String imageUrl =
              FirebaseUploadHelper.uploadFile(field.getInputStream(), fileName, fileExtension);
          System.out.println("Image URL: " + imageUrl);
          images.add(imageUrl);
        } else {
          // Process other form fields
          switch (field.getFieldName()) {
            case "title":
              System.out.println("Title: " + field.getString());
              item.setTitle(field.getString());
              break;
            case "price":
              System.out.println("Price: " + field.getString());
              if (field.getString().isEmpty()) {
                response.status(500);
                responseMap.put("status", 500);
                responseMap.put("message", "Please enter a price for the item.");
                return Utils.toMoshiJson(responseMap);
              }
              // if price is not a number,  throw an exception
              if (!field.getString().matches("\\d+(\\.\\d+)?")) {
                response.status(500);
                responseMap.put("status", 500);
                responseMap.put("message", "Price must be a number");
                return Utils.toMoshiJson(responseMap);
              }
              item.setPrice(Double.parseDouble(field.getString()));
              break;
            case "status":
              System.out.println("Status: " + field.getString());
              item.setStatus(field.getString());
              break;
            case "condition":
              System.out.println("Condition: " + field.getString());
              item.setCondition(field.getString());
              break;
            case "description":
              System.out.println("Description: " + field.getString());
              item.setDescription(field.getString());
              break;
            case "category":
              System.out.println("Category: " + field.getString());
              item.setCategory(field.getString());
              break;
            case "seller":
              System.out.println("Seller: " + field.getString());
              item.setSeller(field.getString());
              break;
          }
        }
      }
      item.setImages(images);
      if (item.checkAnyEmpty()) {
        System.out.println("Missing parameters");
        responseMap.put("status", 500);
        responseMap.put("message", "Please fill in every field.");
        return Utils.toMoshiJson(responseMap);
      }
      String itemId = FirestoreHelper.saveItem(item); // Save item details to Firestore
      responseMap.put("status", 200);
      responseMap.put("message", "Item posted successfully");
      responseMap.put("itemId", itemId);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("status", 500);
      responseMap.put("message", "Fail to post item due to some errors");
      System.out.printf("Fail to post item", e.getMessage());
    }
    return Utils.toMoshiJson(responseMap);
  }
}
