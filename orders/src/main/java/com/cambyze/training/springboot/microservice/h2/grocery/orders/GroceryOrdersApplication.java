package com.cambyze.training.springboot.microservice.h2.grocery.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Spring boot application with API REST documentation by Swagger at the url
 * http://localhost:9090/swagger-ui/
 */
@SpringBootApplication
public class GroceryOrdersApplication {

  public static void main(String[] args) {
    SpringApplication.run(GroceryOrdersApplication.class, args);
  }

}
