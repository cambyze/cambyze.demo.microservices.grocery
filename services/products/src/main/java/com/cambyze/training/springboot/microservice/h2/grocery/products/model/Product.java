package com.cambyze.training.springboot.microservice.h2.grocery.products.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import com.cambyze.commons.microservices.model.PersistEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Persistence entity for products
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@Entity
@Table(indexes = {@Index(columnList = "reference", name = "indProductReference", unique = true)})
public class Product extends PersistEntity {

  @Transient
  private static final String ENTITY_NAME = "product";

  @Id
  @SequenceGenerator(name = "productSequence", initialValue = 1, allocationSize = 10)
  @GeneratedValue(generator = "productSequence")
  @JsonIgnore
  private long id;

  @NotBlank
  @Length(min = 5, max = 50)
  private String reference;

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

  public Product() {
    super();
  }

  public Product(String reference) {
    super(reference);
  }

  public Product(long id, @NotBlank @Length(min = 5, max = 50) String reference,
      @Length(min = 3, max = 255) String name, @URL String imageURL,
      @Min(0) @Max(100000) Double price, @Min(0) @Max(100000) Double purchasePrice,
      @Min(0) @Max(10000000) Integer available) {
    super();
    this.id = id;
    this.reference = reference;
    this.name = name;
    this.imageURL = imageURL;
    this.price = price;
    this.purchasePrice = purchasePrice;
    this.available = available;
  }

  /*
   * Format reference a upper case String
   */
  private void formatProductReference() {
    if (this.reference != null) {
      this.reference = this.reference.toUpperCase().trim();
    }
  }


  @PrePersist
  private void prePersist() {
    formatProductReference();
  }

  @PreUpdate
  private void preUpate() {}

  public String getEntityName() {
    return ENTITY_NAME;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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
    return "Product{id=" + id + ",reference=" + reference + ",name=" + name + ", image=" + imageURL
        + ",price=" + price + ", purchase price=" + purchasePrice + ",quantity available="
        + available + "}";
  }
}
