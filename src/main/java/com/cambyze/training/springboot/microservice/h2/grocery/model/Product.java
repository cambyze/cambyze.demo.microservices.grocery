package com.cambyze.training.springboot.microservice.h2.grocery.model;

public class Product {
  private long id;
  private String name;
  private double price;
  private int available;

  public Product() {
    super();
  }

  public Product(long id, String name, double price, int available) {
    super();
    this.id = id;
    this.name = name;
    this.price = price;
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

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getAvailable() {
    return available;
  }

  public void setAvailable(int available) {
    this.available = available;
  }

  @Override
  public String toString() {
    return "Product{id=" + id + ",name=" + name + ",price=" + price + ",quantity available="
        + available + "}";
  }
}
