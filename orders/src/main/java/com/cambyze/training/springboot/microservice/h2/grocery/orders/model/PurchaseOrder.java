package com.cambyze.training.springboot.microservice.h2.grocery.orders.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import com.cambyze.commons.DateTools;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(
    indexes = {@Index(columnList = "reference", name = "indPurchaseOrderReference", unique = true),
        @Index(columnList = "productReference", name = "indPurchaseOrderProductReference",
            unique = false)})
public class PurchaseOrder {

  @Id
  @SequenceGenerator(name = "purchaseOrderSequence", initialValue = 1, allocationSize = 10)
  @GeneratedValue(generator = "purchaseOrderSequence")
  @JsonIgnore
  private long id;

  @NotBlank
  @Length(min = 5, max = 50)
  private String reference;

  @NotBlank
  @Length(min = 5, max = 50)
  private String productReference;

  private Date orderDate;

  @Min(value = 1)
  @Max(value = 10000000)
  private Integer quantity;

  private Boolean paid;

  /*
   * Format references as upper case String
   */
  private void formatReferences() {
    if (this.reference != null) {
      this.reference = this.reference.toUpperCase().trim();
    }
    if (this.productReference != null) {
      this.productReference = this.productReference.toUpperCase().trim();
    }
  }

  /*
   * Format external references as upper case String
   */
  private void formatExternalReferences() {
    if (this.productReference != null) {
      this.productReference = this.productReference.toUpperCase().trim();
    }
  }

  @PrePersist
  private void prePersist() {
    formatReferences();
    this.orderDate = DateTools.dateWithTimeAtStartOfDay(this.orderDate);
  }

  @PreUpdate
  private void preUpate() {
    formatExternalReferences();
    this.orderDate = DateTools.dateWithTimeAtStartOfDay(this.orderDate);
  }


  public PurchaseOrder() {
    super();
  }

  public PurchaseOrder(long id, @NotBlank @Length(min = 5, max = 50) String reference,
      @NotBlank @Length(min = 5, max = 50) String productReference, Date orderDate,
      @Min(1) @Max(10000000) Integer quantity, Boolean paid) {
    super();
    this.id = id;
    this.reference = reference;
    this.productReference = productReference;
    this.orderDate = orderDate;
    this.quantity = quantity;
    this.paid = paid;
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

  public String getProductReference() {
    return productReference;
  }

  public void setProductReference(String productReference) {
    this.productReference = productReference;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Boolean getPaid() {
    return paid;
  }

  public void setPaid(Boolean paid) {
    this.paid = paid;
  }

  @Override
  public String toString() {
    return "Order{id=" + id + ", reference=" + reference + ", product=" + productReference
        + ", date of order=" + orderDate + ",quantity=" + quantity + ", paid=" + paid + "}";
  }



}
