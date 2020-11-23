package com.cambyze.training.springboot.microservice.h2.groceryPortal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.cambyze.training.springboot.microservice.h2.groceryPortal")
public class GroceryPortalApplication {

  public static void main(String[] args) {
    SpringApplication.run(GroceryPortalApplication.class, args);
  }

}
