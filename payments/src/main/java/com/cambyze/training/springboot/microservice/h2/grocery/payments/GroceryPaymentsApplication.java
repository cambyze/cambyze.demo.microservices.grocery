package com.cambyze.training.springboot.microservice.h2.grocery.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring boot application for payments management
 * <p>
 * with API REST documentation by Swagger
 * 
 * @author Thierry Nestelhut
 * @see <a href="http://localhost:9092/swagger-ui/">API documentation</a>
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@SpringBootApplication(
    scanBasePackages = {"com.cambyze.training.springboot.microservice.h2.grocery.payments",
        "com.cambyze.commons.microservices.web.controller.impl"})
public class GroceryPaymentsApplication {

  public static void main(String[] args) {
    SpringApplication.run(GroceryPaymentsApplication.class, args);
  }

}
