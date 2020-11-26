package com.cambyze.demo.microservices.grocery.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Spring boot application for orders management
 * <p>
 * with API REST documentation by Swagger
 * 
 * @author Thierry Nestelhut
 * @see <a href="http://localhost:9103/demo-orders/swagger-ui/">API documentation</a>
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@SpringBootApplication(scanBasePackages = {"com.cambyze.demo.microservices.grocery.orders",
    "com.cambyze.commons.microservices.web.controller.impl"})
@EnableDiscoveryClient
public class GroceryOrdersApplication {

  public static void main(String[] args) {
    SpringApplication.run(GroceryOrdersApplication.class, args);
  }

}
