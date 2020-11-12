package com.cambyze.training.springboot.microservice.h2.grocery.products.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 5825205427275359216L;

  /**
   * Exception triggered when the product doesn't exist
   */

  public ProductNotFoundException(String code) {
    super("Product with the requested code " + code + " doesn't exist");
  }

  public ProductNotFoundException() {
    super("No existing product for the requested parameters");
  }

}
