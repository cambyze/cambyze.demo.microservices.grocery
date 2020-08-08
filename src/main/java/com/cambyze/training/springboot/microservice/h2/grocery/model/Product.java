package com.cambyze.training.springboot.microservice.h2.grocery.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

@Entity
public class Product {

  @Id
  @GeneratedValue
  private long id;

  @Length(min = 3, max = 255)
  private String name;

  @Min(value = 0)
  @Max(value = 100000)
  private double price;

  @Min(value = 0)
  @Max(value = 10000000)
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
