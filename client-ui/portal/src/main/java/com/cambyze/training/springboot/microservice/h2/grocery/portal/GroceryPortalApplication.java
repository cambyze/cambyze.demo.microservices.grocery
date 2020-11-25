package com.cambyze.training.springboot.microservice.h2.grocery.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.cambyze.training.springboot.microservice.h2.grocery.portal")
public class GroceryPortalApplication {

  public static void main(String[] args) {
    SpringApplication.run(GroceryPortalApplication.class, args);
  }

}
