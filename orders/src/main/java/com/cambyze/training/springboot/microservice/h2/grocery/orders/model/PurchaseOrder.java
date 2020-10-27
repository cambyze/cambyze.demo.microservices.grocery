package com.cambyze.training.springboot.microservice.h2.grocery.orders.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PurchaseOrder {

  @Id
  @GeneratedValue
  private long id;


  private long productReference;

  private Date orderDate;

  private Integer quantity;

  private Boolean paid;


  public PurchaseOrder() {
    super();
  }

  public PurchaseOrder(long id) {
    super();
    this.id = id;
  }

  public PurchaseOrder(long id, long productReference, Date orderDate, Integer quantity,
      Boolean paid) {
    super();
    this.id = id;
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


  public long getProductReference() {
    return productReference;
  }

  public void setProductReference(long productReference) {
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
    return "Order{id=" + id + ",product=" + productReference + ", date of order=" + orderDate
        + ",quantity=" + quantity + ", paid=" + paid + "}";
  }



}
