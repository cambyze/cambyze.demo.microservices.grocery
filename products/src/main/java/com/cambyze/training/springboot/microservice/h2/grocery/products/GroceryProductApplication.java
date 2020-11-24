package com.cambyze.training.springboot.microservice.h2.grocery.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Spring boot application for products management
 * <p>
 * with API REST documentation by Swagger
 * 
 * @author Thierry Nestelhut
 * @see <a href="http://localhost:9090/swagger-ui/">API documentation</a>
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@SpringBootApplication(
    scanBasePackages = {"com.cambyze.training.springboot.microservice.h2.grocery.products",
        "com.cambyze.commons.microservices.web.controller.impl"})
@EnableConfigurationProperties
public class GroceryProductApplication {

  public static void main(String[] args) {
    SpringApplication.run(GroceryProductApplication.class, args);
  }

}
