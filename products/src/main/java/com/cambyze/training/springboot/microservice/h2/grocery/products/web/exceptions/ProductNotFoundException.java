package com.cambyze.training.springboot.microservice.h2.grocery.products.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.cambyze.commons.microservices.web.exceptions.RecordNotFoundException;

/**
 * Product exception
 * 
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RecordNotFoundException {

  private static final long serialVersionUID = 5825205427275359216L;

  /**
   * Exception triggered when the product doesn't exist with this reference
   * 
   * @param productReference reference of the product
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
