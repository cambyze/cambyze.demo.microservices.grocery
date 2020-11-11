package com.cambyze.training.springboot.microservice.h2.grocery.products.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Product {

  @Id
  @GeneratedValue
  private long id;

  @Length(min = 3, max = 255)
  private String name;

  @Length(min = 3, max = 4000)
  private String image;

  @Min(value = 0)
  @Max(value = 100000)
  private double price;

  @Min(value = 0)
  @Max(value = 100000)
  @JsonIgnore
  private double purchasePrice;

  @Min(value = 0)
  @Max(value = 10000000)
  private int available;

  public Product() {
    super();
  }

  public Product(long id, @Length(min = 3, max = 255) String name,
      @Length(min = 3, max = 4000) String image, @Min(0) @Max(100000) double price,
      @Min(0) @Max(100000) double purchasePrice, @Min(0) @Max(10000000) int available) {
    super();
    this.id = id;
    this.name = name;
    this.image = image;
    this.price = price;
    this.purchasePrice = purchasePrice;
    this.available = available;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public int getAvailable() {
    return available;
  }

  public void setAvailable(int available) {
    this.available = available;
  }

  @Override
  public String toString() {
    return "Product{id=" + id + ",name=" + name + ", image=" + image + ",price=" + price
        + ", purchase price=" + purchasePrice + ",quantity available=" + available + "}";
  }
}
