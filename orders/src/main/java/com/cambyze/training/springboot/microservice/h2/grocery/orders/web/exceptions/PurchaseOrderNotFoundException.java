package com.cambyze.training.springboot.microservice.h2.grocery.orders.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.cambyze.commons.microservices.web.exceptions.RecordNotFoundException;

/**
 * Purchase order exception
 * 
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PurchaseOrderNotFoundException extends RecordNotFoundException {

  private static final long serialVersionUID = 3323550819529587937L;

  /**
   * Exception triggered when the order doesn't exist with this reference
   * 
   * @param orderReference reference of the purchase order
   */
  public PurchaseOrderNotFoundException(String orderReference) {
    super("Purchase order with the requested reference=" + orderReference + " doesn't exist");
  }

  /**
   * Exception triggered when the order doesn't exist
   */
  public PurchaseOrderNotFoundException() {
    super("No existing purchase order for the requested parameters");
  }

}
