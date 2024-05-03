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
      System.out.println("getting a post request");
      System.out.println(formItems);
      Item item = new Item(); // Assuming Item class exists
      List<String> images = new ArrayList<>();

      for (FileItem field : formItems) {
        if (!field.isFormField()) {
          String fileName = field.getName();
          String fileExtension = fileName.substring(fileName.lastIndexOf("."));
          if (!fileExtension.equals(".jpg") && !fileExtension.equals(".jpeg")) {
            responseMap.put("status", 500);
            responseMap.put("message", "Please only upload jpg/jpeg images");
            return Utils.toMoshiJson(responseMap);
          }
          String imageUrl = FirebaseUploadHelper.uploadFile(field.getInputStream(), fileName);
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
              // if price is not a number,  throw an exception
              if (!field.getString().matches("\\d+(\\.\\d+)?")) {
                responseMap.put("status", 500);
                responseMap.put("message", "Price must be a number/missing price field");
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
        responseMap.put("message", "Fail to post item with missing parameters: ");
        return Utils.toMoshiJson(responseMap);
      }
      FirestoreHelper.saveItem(item); // Save item details to Firestore
      responseMap.put("status", 200);
      responseMap.put("message", "Item posted successfully");
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("status", 500);
      responseMap.put("message", "Fail to post item: " + e.getMessage());
    }
    return Utils.toMoshiJson(responseMap);
  }
}
