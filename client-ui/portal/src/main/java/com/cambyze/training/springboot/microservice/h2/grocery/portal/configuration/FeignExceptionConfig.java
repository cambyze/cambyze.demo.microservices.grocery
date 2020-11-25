package com.cambyze.training.springboot.microservice.h2.grocery.portal.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cambyze.training.springboot.microservice.h2.grocery.portal.exceptions.MicroservicesExceptionsController;

@Configuration
public class FeignExceptionConfig {

  @Bean
  public MicroservicesExceptionsController microservicesExceptionsController() {
    return new MicroservicesExceptionsController();
  };

}
