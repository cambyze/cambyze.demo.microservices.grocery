package com.cambyze.training.springboot.microservice.h2.grocery.products.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PurchaseOrderNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 6985202842556589466L;

  /**
   * Exception triggered when the order doesn't exist
   */

  public PurchaseOrderNotFoundException(long id) {
    super("Order with the requested id=" + id + " doesn't exist");
  }

  public PurchaseOrderNotFoundException() {
    super("No existing order for the requested parameters");
  }

}
