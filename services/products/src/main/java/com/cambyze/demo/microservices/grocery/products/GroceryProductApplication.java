package com.cambyze.demo.microservices.grocery.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Spring boot application for products management
 * <p>
 * with API REST documentation by Swagger
 * 
 * @author Thierry Nestelhut
 * @see <a href="http://localhost:9103/demo-products/swagger-ui/">API documentation</a>
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@SpringBootApplication(scanBasePackages = {"com.cambyze.demo.microservices.grocery.products",
    "com.cambyze.commons.microservices.web.controller.impl"})
@EnableConfigurationProperties
@EnableDiscoveryClient
public class GroceryProductApplication {

  public static void main(String[] args) {
    SpringApplication.run(GroceryProductApplication.class, args);
  }

}
