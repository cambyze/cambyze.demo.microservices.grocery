package com.cambyze.training.springboot.microservice.h2.grocery.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Spring boot application with API REST documentation by Swagger at the url
 * http://localhost:9090/swagger-ui/
 */
@SpringBootApplication(
    scanBasePackages = {"com.cambyze.training.springboot.microservice.h2.grocery.products",
        "com.cambyze.commons.microservices.controller.impl"})
public class GroceryProductApplication {

  public static void main(String[] args) {
    SpringApplication.run(GroceryProductApplication.class, args);
  }

}
