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
import com.cambyze.commons.microservices.model.PersistEntity;
import com.cambyze.commons.tools.DateTools;
import com.cambyze.commons.tools.MathTools;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * Persistence entity for orders
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@Entity
@Table(
    indexes = {@Index(columnList = "reference", name = "indPurchaseOrderReference", unique = true),
        @Index(columnList = "productReference", name = "indPurchaseOrderProductReference",
            unique = false)})
public class PurchaseOrder extends PersistEntity {

  private static final String ENTITY_NAME = "purchase order";

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

  @Min(value = 0)
  @Max(value = 100000)
  private Double amount;

  @Min(value = 0)
  @Max(value = 100000)
  private Double balance;

  private Boolean paid;

  public String getEntityName() {
    return ENTITY_NAME;
  }

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

  /**
   * Format external references as upper case String
   */
  private void formatExternalReferences() {
    if (this.productReference != null) {
      this.productReference = this.productReference.toUpperCase().trim();
    }
  }

  /*
   * Format received numbers as amounts
   */
  private void formatAmounts() {
    if (this.amount != null) {
      this.amount = MathTools.roundWithDecimals(this.amount, NBDECIMALS);
    }
    if (this.balance != null) {
      this.balance = MathTools.roundWithDecimals(this.balance, NBDECIMALS);
    }
  }

  /**
   * Executed before insertion
   */
  @PrePersist
  private void prePersist() {
    formatReferences();
    formatAmounts();
    this.orderDate = DateTools.dateWithTimeAtStartOfDay(this.orderDate);
  }

  /**
   * Executed before update
   */
  @PreUpdate
  private void preUpate() {
    formatExternalReferences();
    formatAmounts();
    this.orderDate = DateTools.dateWithTimeAtStartOfDay(this.orderDate);
  }


  public PurchaseOrder() {
    super();
  }

  public PurchaseOrder(long id, @NotBlank @Length(min = 5, max = 50) String reference,
      @NotBlank @Length(min = 5, max = 50) String productReference, Date orderDate,
      @Min(1) @Max(10000000) Integer quantity, @Min(0) @Max(100000) Double amount,
      @Min(0) @Max(100000) Double balance, Boolean paid) {
    super(reference);
    this.id = id;
    this.productReference = productReference;
    this.orderDate = orderDate;
    this.quantity = quantity;
    this.amount = amount;
    this.balance = balance;
    this.paid = paid;
  }

  public PurchaseOrder(@NotBlank @Length(min = 5, max = 50) String reference) {
    super();
    this.reference = reference;
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

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
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
