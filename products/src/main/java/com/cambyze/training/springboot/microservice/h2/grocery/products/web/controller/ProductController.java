package com.cambyze.training.springboot.microservice.h2.grocery.products.web.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.cambyze.commons.MathTools;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
  private static final int NBDECIMALS = 2;

  @Autowired
  private ProductDao productDao;

  private void formatProductNumber(Product product) {
    if (product != null) {
      if (product.getPrice() != null) {
        product.setPrice(MathTools.roundWithDecimals(product.getPrice(), NBDECIMALS));
      }
      if (product.getPurchasePrice() != null) {
        product
            .setPurchasePrice(MathTools.roundWithDecimals(product.getPurchasePrice(), NBDECIMALS));
      }
    }
  }

  public class ProductMargin {
    private Product product;
    private Double unitMargin;
    private Double potentialMargin;

    public ProductMargin() {
      super();
    }

    public ProductMargin(Product product, double unitaryMargin, double potentialMargin) {
      super();
      this.product = product;
      this.unitMargin = unitaryMargin;
      this.potentialMargin = potentialMargin;
    }

    public Product getProduct() {
      return product;
    }

    public void setProduct(Product product) {
      this.product = product;
    }


    /*
     * Get the unit margin
     */
    public double getUnitMargin() {
      return unitMargin;
    }

    public void setUnitMargin(double unitMargin) {
      this.unitMargin = unitMargin;
    }

    /*
     * Get the potential margin according to the stock
     */
    public double getPotentialMargin() {
      return potentialMargin;
    }

    public void setPotentialMargin(double potentialMargin) {
      this.potentialMargin = potentialMargin;
    }
  }

  /*
   * Get product by its product code
   */
  @ApiOperation(value = "Retrieve a product with its product code")
  @GetMapping(value = "/products/{code}")
  public Product getProductbyCode(@PathVariable String code) throws RuntimeException {
    Product product = productDao.findByCode(code);
    if (product == null) {
      throw new ProductNotFoundException(code);
    } else {
      LOGGER.info("Product code:" + code + " = " + product);
      return product;
    }
  }

  /*
   * Find products available for at least the requested quantity
   */
  @ApiOperation(value = "Find products available for at least the requested quantity")
  @GetMapping(value = "/products")
  public List<Product> getProducts(
      @RequestParam(value = "quantityMin", defaultValue = "0") int quantityMin) {
    if (quantityMin < 0) {
      quantityMin = 0;
    }
    List<Product> products = productDao.findByAvailableGreaterThan(quantityMin);
    if (products == null || products.isEmpty()) {
      throw new ProductNotFoundException();
    } else {
      LOGGER.info(
          "Products avalaibles for the min quantity " + quantityMin + " = " + products.size());
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
      Double unitMargin = 0.0;
      Double potentialMargin = 0.0;
      if (product.getPrice() != null && product.getPurchasePrice() != null) {
        unitMargin = MathTools.roundWithDecimals(product.getPrice() - product.getPurchasePrice(),
            NBDECIMALS);
        if (product.getAvailable() != null) {
          potentialMargin =
              MathTools.roundWithDecimals(unitMargin * product.getAvailable(), NBDECIMALS);
        }
      }
      LOGGER.info("Margins for the product " + product.getCode() + " = " + unitMargin + " , "
          + potentialMargin);
      productsMargins.add(new ProductMargin(product, unitMargin, potentialMargin));
    }
    if (productsMargins == null || productsMargins.isEmpty()) {
      throw new ProductNotFoundException();
    } else {
      return productsMargins;
    }
  }


  @ApiOperation(value = "Create a new product in the inventory")
  @PostMapping(value = "/products")
  public ResponseEntity<Object> createProduct(@Valid @RequestBody Product product) {
    formatProductNumber(product);
    Product newProduct = productDao.save(product);
    if (newProduct == null) {
      return ResponseEntity.noContent().build();
    } else {
      LOGGER.info("Create product " + product.getCode() + " with values = " + product);
      URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{code}")
          .buildAndExpand(newProduct.getCode()).toUri();
      return ResponseEntity.created(uri).build();
    }
  }

  @ApiOperation(value = "Remove a product from the inventory")
  @DeleteMapping(value = "/products/{code}")
  public void deleteProduct(@PathVariable String code) throws RuntimeException {
    Product product = productDao.findByCode(code);
    if (product != null) {
      LOGGER.info("Remove product " + code + " with values = " + product);
      productDao.deleteById(product.getId());
    } else {
      throw new ProductNotFoundException(code);
    }
  }

  @ApiOperation(value = "Modify all the attributes of a product of the inventory")
  @PutMapping(value = "/products/{code}")
  public void updateProduit(@RequestBody Product product, @PathVariable String code)
      throws RuntimeException {
    Product existingProduct = productDao.findByCode(code);
    if (existingProduct != null && product != null) {
      product.setId(existingProduct.getId());
      product.setCode(code);
      formatProductNumber(product);
      LOGGER.info("Full update of product " + code + " with values = " + product);
      productDao.save(product);
    } else {
      throw new ProductNotFoundException(code);
    }
  }

  @ApiOperation(value = "Modify some attributes of a product of the inventory")
  @PatchMapping(value = "/products/{code}")
  public void partialUpdateProduit(@RequestBody Product product, @PathVariable String code)
      throws RuntimeException {
    Product existingProduct = productDao.findByCode(code);
    if (existingProduct != null && product != null) {
      product.setId(existingProduct.getId());
      product.setCode(code);
      formatProductNumber(product);
      LOGGER.info("Partial update of product " + code + " with values = " + product);
      if (product.getAvailable() != null) {
        existingProduct.setAvailable(product.getAvailable());
      }
      if (product.getImageURL() != null) {
        existingProduct.setImageURL(product.getImageURL());
      }
      if (product.getName() != null) {
        existingProduct.setName(product.getName());
      }
      if (product.getPrice() != null) {
        existingProduct.setPrice(product.getPrice());
      }
      if (product.getPurchasePrice() != null) {
        existingProduct.setPurchasePrice(product.getPurchasePrice());
      }
      productDao.save(existingProduct);
    } else {
      throw new ProductNotFoundException(code);
    }
  }

}
