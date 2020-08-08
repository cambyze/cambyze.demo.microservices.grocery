package com.cambyze.training.springboot.microservice.h2.grocery.web.controller;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.cambyze.training.springboot.microservice.h2.grocery.dao.ProductDao;
import com.cambyze.training.springboot.microservice.h2.grocery.model.Product;
import com.cambyze.training.springboot.microservice.h2.grocery.web.exceptions.ProductNotFoundException;

@RestController
public class ProductController {

  @Autowired
  private ProductDao productDao;

  // Get product by id
  @GetMapping(value = "/products/{id}")
  public Product getProductbyId(@PathVariable long id) throws RuntimeException {
    Product product = productDao.findById(id);
    if (product == null) {
      throw new ProductNotFoundException(id);
    } else {
      return product;
    }
  }

  // Find products available for at least the requested quantity
  @GetMapping(value = "/products")
  public List<Product> getProducts() {
    int quantityMin = 25;
    List<Product> products = productDao.findByAvailableGreaterThan(quantityMin);
    return products;
  }

  @PostMapping(value = "/products")
  public ResponseEntity<Object> createProduct(@RequestBody Product product) {
    Product newProduct = productDao.save(product);
    if (newProduct == null) {
      return ResponseEntity.noContent().build();
    } else {
      URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
          .buildAndExpand(newProduct.getId()).toUri();
      return ResponseEntity.created(uri).build();
    }
  }

  @DeleteMapping(value = "/products/{id}")
  public void deleteProduct(@PathVariable long id) {
    productDao.deleteById(id);
  }


}
