package edu.brown.cs.student.main.server.storage;

import java.util.List;

public class Item {
  private String title;
  private double price;
  private String status;
  private String condition;
  private String description;
  private String category;
  private List<String> imageUrls;

  public void item() {
    this.title = "";
    this.price = 0.0;
    this.status = "";
    this.condition = "";
    this.description = "";
    this.category = "";
    this.imageUrls = null;
  }

  public void item(
      String title,
      double price,
      String status,
      String condition,
      String description,
      String category,
      String imageUrl) {
    this.title = title;
    this.price = price;
    this.status = status;
    this.condition = condition;
    this.description = description;
    this.category = category;
    this.imageUrls = null;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public List<String> getImageUrls() {
    return imageUrls;
  }

  public void setImageUrls(List<String> imageUrls) {
    this.imageUrls = imageUrls;
  }

  public boolean checkAnyEmpty() {
    return this.title == null
        || this.status == null
        || this.condition == null
        || this.description == null
        || this.category == null
        || this.imageUrls == null;
  }
}
