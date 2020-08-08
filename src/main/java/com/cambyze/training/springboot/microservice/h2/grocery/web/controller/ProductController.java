package com.cambyze.training.springboot.microservice.h2.grocery.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.cambyze.training.springboot.microservice.h2.grocery.model.Product;

@RestController
public class ProductController {
  @GetMapping(value = "/products/{id}")
  public Product getProduct(@PathVariable long id) {
    return new Product(id, "Screwdriver 45", 12.5, 56);
  }
}
