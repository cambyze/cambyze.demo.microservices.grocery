package com.cambyze.training.springboot.microservice.h2.grocery.products.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.cambyze.commons.microservices.web.exceptions.EntityMandatoryAttributeException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductMandatoryReferenceException extends EntityMandatoryAttributeException {

  private static final long serialVersionUID = 8373143065668512694L;

  /**
   * Exception triggered when the product reference is not specified in the request
   */
  public ProductMandatoryReferenceException() {
    super("The product reference is mandatory");
  }

}
