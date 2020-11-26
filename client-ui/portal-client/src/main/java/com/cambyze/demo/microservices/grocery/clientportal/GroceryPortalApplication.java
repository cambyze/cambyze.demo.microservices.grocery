package com.cambyze.demo.microservices.grocery.clientportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Portal client of the grocery
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.cambyze.demo.microservices.grocery.clientportal")
public class GroceryPortalApplication {

  public static void main(String[] args) {
    SpringApplication.run(GroceryPortalApplication.class, args);
  }

}
