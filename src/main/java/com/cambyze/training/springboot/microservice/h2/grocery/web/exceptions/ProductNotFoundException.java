package com.cambyze.training.springboot.microservice.h2.grocery.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

  /**
   * Exception triggered when the product doesn't exist
   */
  private static final long serialVersionUID = 427284400387327228L;

  public ProductNotFoundException(long id) {
    super("Product with id=" + id + " doesn't exist");
  }
}
