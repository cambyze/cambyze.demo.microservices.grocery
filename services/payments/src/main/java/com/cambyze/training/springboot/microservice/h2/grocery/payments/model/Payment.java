package com.cambyze.training.springboot.microservice.h2.grocery.payments.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.cambyze.commons.microservices.model.PersistEntity;
import com.cambyze.commons.tools.DateTools;
import com.cambyze.commons.tools.MathTools;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * Persistence entity for payments
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@Entity
@Table(indexes = {@Index(columnList = "reference", name = "indPaymentReference", unique = true),
    @Index(columnList = "orderReference", name = "indPaymentOrderReference", unique = false)})
public class Payment extends PersistEntity {

  private static final String ENTITY_NAME = "payment";

  @Id
  @SequenceGenerator(name = "paymentSequence", initialValue = 1, allocationSize = 10)
  @GeneratedValue(generator = "paymentSequence")
  @JsonIgnore
  private long id;

  @NotBlank
  @Length(min = 5, max = 50)
  private String reference;

  @NotBlank
  @Length(min = 5, max = 50)
  private String orderReference;

  @NotNull
  private Date paymentDate;

  @NotNull
  @Min(value = 0)
  @Max(value = 100000)
  private Double paymentAmount;

  @NotNull
  @Enumerated(EnumType.STRING)
  private PaymentStatus status = PaymentStatus.VALID;

  @Length(min = 5, max = 50)
  private String maskedCardNumber;


  public String getEntityName() {
    return ENTITY_NAME;
  }


  public Payment() {
    super();
  }

  public Payment(@NotBlank @Length(min = 5, max = 50) String reference) {
    super();
    this.reference = reference;
  }



  public Payment(long id, @NotBlank @Length(min = 5, max = 50) String reference,
      @NotBlank @Length(min = 5, max = 50) String orderReference, @NotNull Date paymentDate,
      @NotNull @Min(0) @Max(100000) Double paymentAmount, @NotBlank PaymentStatus status,
      @NotBlank @Length(min = 5, max = 50) String maskedCardNumber) {
    super();
    this.id = id;
    this.reference = reference;
    this.orderReference = orderReference;
    this.paymentDate = paymentDate;
    this.paymentAmount = paymentAmount;
    this.status = status;
    this.maskedCardNumber = maskedCardNumber;
  }


  /*
   * Format references as upper case String
   */
  private void formatReferences() {
    if (this.reference != null) {
      this.reference = this.reference.toUpperCase().trim();
    }
    if (this.orderReference != null) {
      this.orderReference = this.orderReference.toUpperCase().trim();
    }
  }

  /**
   * Format external references as upper case String
   */
  private void formatExternalReferences() {
    if (this.orderReference != null) {
      this.orderReference = this.orderReference.toUpperCase().trim();
    }
  }

  /*
   * Format received numbers as amounts
   */
  private void formatAmounts() {
    if (this.paymentAmount != null) {
      this.paymentAmount = MathTools.roundWithDecimals(this.paymentAmount, NBDECIMALS);
    }
  }


  /**
   * Executed before insertion
   */
  @PrePersist
  private void prePersist() {
    formatReferences();
    formatAmounts();
    this.paymentDate = DateTools.dateWithTimeAtStartOfDay(this.paymentDate);
  }

  /**
   * Executed before update
   */
  @PreUpdate
  private void preUpate() {
    formatExternalReferences();
    formatAmounts();
    this.paymentDate = DateTools.dateWithTimeAtStartOfDay(this.paymentDate);
  }


  public String getOrderReference() {
    return orderReference;
  }

  public void setOrderReference(String orderReference) {
    this.orderReference = orderReference;
  }

  public Date getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(Date paymentDate) {
    this.paymentDate = paymentDate;
  }

  public Double getPaymentAmount() {
    return paymentAmount;
  }

  public void setPaymentAmount(Double paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  public PaymentStatus getStatus() {
    return status;
  }


  public void setStatus(PaymentStatus status) {
    this.status = status;
  }


  public String getMaskedCardNumber() {
    return maskedCardNumber;
  }

  public void setMaskedCardNumber(String maskedCardNumber) {
    this.maskedCardNumber = maskedCardNumber;
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

  @Override
  public String toString() {
    return "Payment{id=" + id + ", reference=" + reference + ", order=" + orderReference
        + ", date of payment=" + paymentDate + ", amount=" + paymentAmount + ", status " + status
        + ", card=" + maskedCardNumber + "}";
  }


}
