package com.cambyze.training.springboot.microservice.h2.grocery.portal.exceptions;

import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cambyze.commons.microservices.web.exceptions.RecordNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class MicroservicesExceptionsController implements ErrorDecoder {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MicroservicesExceptionsController.class);

  private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

  @Override
  public Exception decode(String methodKey, Response response) {
    LOGGER.debug("HTTP statut= " + response.status());
    if (response.status() == HttpServletResponse.SC_NOT_FOUND) {
      return new RecordNotFoundException("Element not found");
    }
    return defaultErrorDecoder.decode(methodKey, response);
  }

}
