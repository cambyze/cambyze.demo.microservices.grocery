package com.cambyze.training.springboot.microservice.h2.grocery.portal.beans;

/**
 * Product bean to interface with the microservice Products
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
public class ProductBean {

  private String reference;
  private String name;
  private String imageURL;
  private Double price;
  private Integer available;
  private String textPrice;
  private String textStock;


  public ProductBean() {
    super();
  }


  public ProductBean(String reference, String name, String imageURL, Double price,
      Integer available) {
    super();
    this.reference = reference;
    this.name = name;
    this.imageURL = imageURL;
    this.price = price;
    this.available = available;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
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


  public Integer getAvailable() {
    return available;
  }

  public void setAvailable(Integer available) {
    this.available = available;
  }

  public String getTextPrice() {
    return textPrice;
  }


  public void setTextPrice(String textPrice) {
    this.textPrice = textPrice;
  }


  public String getTextStock() {
    return textStock;
  }


  public void setTextStock(String textStock) {
    this.textStock = textStock;
  }


  @Override
  public String toString() {
    return "ProductBean [reference=" + reference + ", name=" + name + ", image URL=" + imageURL
        + ", price=" + price + ", available=" + available + "]";
  }


}
