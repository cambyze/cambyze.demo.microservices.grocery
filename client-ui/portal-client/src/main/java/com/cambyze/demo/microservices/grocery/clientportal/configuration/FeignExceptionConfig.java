package com.cambyze.demo.microservices.grocery.clientportal.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cambyze.demo.microservices.grocery.clientportal.exceptions.MicroservicesExceptionsController;

@Configuration
public class FeignExceptionConfig {

  @Bean
  public MicroservicesExceptionsController microservicesExceptionsController() {
    return new MicroservicesExceptionsController();
  };

}
