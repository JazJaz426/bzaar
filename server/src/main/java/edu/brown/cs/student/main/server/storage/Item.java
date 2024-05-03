package edu.brown.cs.student.main.server.storage;

import java.util.List;

public class Item {
  private String title;
  private double price;
  private String status;
  private String condition;
  private String description;
  private String category;
  private String seller;
  private List<String> images;

  public void item() {
    this.title = "";
    this.price = 0.0;
    this.status = "";
    this.condition = "";
    this.description = "";
    this.category = "";
    this.seller = "";
    this.images = null;
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
    this.seller = "";
    this.images = null;
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

  public List<String> getImages() {
    return images;
  }

  public void setImages(List<String> images) {
    this.images = images;
  }

  public String getSeller() {
    return seller;
  }

  public void setSeller(String seller) {
    this.seller = seller;
  }

  public boolean checkAnyEmpty() {
    return this.title == null
        || this.title.isEmpty()
        || this.condition == null
        || this.condition.isEmpty()
        || this.status == null
        || this.status.isEmpty()
        || this.condition == null
        || this.condition.isEmpty()
        || this.description == null
        || this.description.isEmpty()
        || this.seller == null
        || this.seller.isEmpty()
        || this.category == null
        || this.category.isEmpty()
        || this.images == null;
  }
}
