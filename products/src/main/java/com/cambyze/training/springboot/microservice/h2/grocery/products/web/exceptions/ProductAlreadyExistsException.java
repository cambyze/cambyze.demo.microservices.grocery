package com.cambyze.training.springboot.microservice.h2.grocery.products.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.cambyze.commons.microservices.web.exceptions.RecordAlreadyExistsException;

/**
 * Product exception
 * 
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductAlreadyExistsException extends RecordAlreadyExistsException {

  private static final long serialVersionUID = 2307460542505704490L;

  /**
   * Exception triggered when a product already exists with this reference
   */
  public ProductAlreadyExistsException(String productReference) {
    super("A product with the requested reference " + productReference + " already exists");
  }

  /**
   * Exception triggered when the product already exists
   */
  public ProductAlreadyExistsException() {
    super("A product already exists with the requested parameters");
  }

}
