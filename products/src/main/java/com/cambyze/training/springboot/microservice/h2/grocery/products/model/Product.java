package com.cambyze.training.springboot.microservice.h2.grocery.products.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(indexes = {@Index(columnList = "code", name = "indProductCode", unique = true)})
public class Product {


  @Id
  @SequenceGenerator(name = "productSequence", initialValue = 200, allocationSize = 10)
  @GeneratedValue(generator = "productSequence")
  @JsonIgnore
  private long id;

  @NotBlank
  @Length(min = 5, max = 50)
  private String code;

  @Length(min = 3, max = 255)
  private String name;

  @URL
  private String imageURL;

  @Min(value = 0)
  @Max(value = 100000)
  private Double price;

  @Min(value = 0)
  @Max(value = 100000)
  private Double purchasePrice;

  @Min(value = 0)
  @Max(value = 10000000)
  private Integer available;


  public long getId() {
    return id;
  }



  public void setId(long id) {
    this.id = id;
  }



  public String getCode() {
    return code;
  }



  public void setCode(String code) {
    this.code = code;
  }



  public String getName() {
    return name;
  }



  public void setName(String name) {
    this.name = name;
  }



  public String getImageURL() {
    return imageURL;
  }



  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }



  public Double getPrice() {
    return price;
  }



  public void setPrice(Double price) {
    this.price = price;
  }



  public Double getPurchasePrice() {
    return purchasePrice;
  }



  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }



  public Integer getAvailable() {
    return available;
  }



  public void setAvailable(Integer available) {
    this.available = available;
  }



  @Override
  public String toString() {
    return "Product{id=" + id + ",code=" + code + ",name=" + name + ", image=" + imageURL
        + ",price=" + price + ", purchase price=" + purchasePrice + ",quantity available="
        + available + "}";
  }
}
