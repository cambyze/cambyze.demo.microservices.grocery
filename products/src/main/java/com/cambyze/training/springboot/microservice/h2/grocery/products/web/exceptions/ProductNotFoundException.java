package com.cambyze.training.springboot.microservice.h2.grocery.products.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.cambyze.commons.microservices.web.exceptions.EntityNotFoundException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends EntityNotFoundException {

  private static final long serialVersionUID = 5825205427275359216L;

  /**
   * Exception triggered when the product doesn't exist this reference
   */
  public ProductNotFoundException(String productReference) {
    super("Product with the requested reference " + productReference + " doesn't exist");
  }

  /**
   * Exception triggered when the product doesn't exist
   */
  public ProductNotFoundException() {
    super("No existing product for the requested parameters");
  }

}
