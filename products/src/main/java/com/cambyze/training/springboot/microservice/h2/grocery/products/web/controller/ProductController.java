package com.cambyze.training.springboot.microservice.h2.grocery.products.web.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.cambyze.training.springboot.microservice.h2.grocery.products.dao.ProductDao;
import com.cambyze.training.springboot.microservice.h2.grocery.products.model.Product;
import com.cambyze.training.springboot.microservice.h2.grocery.products.web.exceptions.ProductNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/*
 * REST API controller for products management
 *
 * API documentation configuration and description in class SwaggerConfig
 */
@RestController
@Api(tags = {"ProductController"})
public class ProductController {

  @Autowired
  private ProductDao productDao;

  public class ProductMargin {
    private Product product;
    private double unitaryMargin;
    private double potentialMargin;

    public ProductMargin() {
      super();
    }

    public ProductMargin(Product product, double unitaryMargin, double potentialMargin) {
      super();
      this.product = product;
      this.unitaryMargin = unitaryMargin;
      this.potentialMargin = potentialMargin;
    }

    public Product getProduct() {
      return product;
    }

    public void setProduct(Product product) {
      this.product = product;
    }

    public double getUnitaryMargin() {
      return unitaryMargin;
    }

    public void setUnitaryMargin(double unitaryMargin) {
      this.unitaryMargin = unitaryMargin;
    }

    public double getPotentialMargin() {
      return potentialMargin;
    }

    public void setPotentialMargin(double potentialMargin) {
      this.potentialMargin = potentialMargin;
    }
  }

  /*
   * Get product by id
   */
  @ApiOperation(value = "Retrieve a product with its id")
  @GetMapping(value = "/products/{id}")
  public Product getProductbyId(@PathVariable long id) throws RuntimeException {
    Product product = productDao.findById(id);
    if (product == null) {
      throw new ProductNotFoundException(id);
    } else {
      return product;
    }
  }

  /*
   * Find products available for at least the requested quantity
   */
  @ApiOperation(value = "Find products available for at least the requested quantity")
  @GetMapping(value = "/products")
  public List<Product> getProducts() {
    int quantityMin = 25;
    List<Product> products = productDao.findByAvailableGreaterThan(quantityMin);
    if (products == null || products.isEmpty()) {
      throw new ProductNotFoundException();
    } else {
      return products;
    }

  }


  /*
   * Calculate margin per product
   */
  @ApiOperation(value = "Calculate margin per product")
  @GetMapping(value = "/products/margins")
  public List<ProductMargin> getProductsMargins() {
    List<ProductMargin> productsMargins = new ArrayList<ProductMargin>();
    List<Product> products = productDao.findAll();
    for (Product product : products) {
      productsMargins
          .add(new ProductMargin(product, product.getPrice() - product.getPurchasePrice(),
              (product.getPrice() - product.getPurchasePrice()) * product.getAvailable()));
    }
    return productsMargins;
  }


  @ApiOperation(value = "Create a new product in the inventory")
  @PostMapping(value = "/products")
  public ResponseEntity<Object> createProduct(@Valid @RequestBody Product product) {
    Product newProduct = productDao.save(product);
    if (newProduct == null) {
      return ResponseEntity.noContent().build();
    } else {
      URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
          .buildAndExpand(newProduct.getId()).toUri();
      return ResponseEntity.created(uri).build();
    }
  }

  @ApiOperation(value = "Remove a product from the inventory")
  @DeleteMapping(value = "/products/{id}")
  public void deleteProduct(@PathVariable long id) {
    productDao.deleteById(id);
  }

  @ApiOperation(value = "Modify a product of the inventory")
  @PutMapping(value = "/products/{id}")
  public void updateProduit(@RequestBody Product product, @PathVariable long id) {
    product.setId(id);
    productDao.save(product);
  }

}
