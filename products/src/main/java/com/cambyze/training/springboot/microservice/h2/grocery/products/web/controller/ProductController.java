package com.cambyze.training.springboot.microservice.h2.grocery.products.web.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.cambyze.commons.microservices.controller.MicroserviceControllerService;
import com.cambyze.commons.microservices.model.MicroserviceResponseBody;
import com.cambyze.training.springboot.microservice.h2.grocery.products.dao.ProductDao;
import com.cambyze.training.springboot.microservice.h2.grocery.products.model.Product;
import com.cambyze.training.springboot.microservice.h2.grocery.products.web.exceptions.ProductAlreadyExistsException;
import com.cambyze.training.springboot.microservice.h2.grocery.products.web.exceptions.ProductMandatoryReferenceException;
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
  private static final String END_OF_PATH_PRODUCT = "/products";

  @Autowired
  private ProductDao productDao;

  @Autowired
  private MicroserviceControllerService microserviceControllerService;


  /*
   * Response body for calculated margins
   */
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

    public Double getUnitMargin() {
      return unitMargin;
    }

    public void setUnitMargin(Double unitMargin) {
      this.unitMargin = unitMargin;
    }

    public Double getPotentialMargin() {
      return potentialMargin;
    }

    public void setPotentialMargin(Double potentialMargin) {
      this.potentialMargin = potentialMargin;
    }
  }


  /*
   * Get product by its product reference
   */
  @ApiOperation(value = "Retrieve a product with its product reference")
  @GetMapping(value = END_OF_PATH_PRODUCT + "/{reference}")
  public Product getProductbyReference(@PathVariable String reference) throws RuntimeException {
    if (reference != null && !reference.isBlank()) {
      reference = reference.toUpperCase().trim();
      Product product = productDao.findByReference(reference);
      if (product == null) {
        throw new ProductNotFoundException(reference);
      } else {
        LOGGER.info("Product reference:" + reference + " = " + product);
        return product;
      }
    } else {
      throw new ProductMandatoryReferenceException();
    }
  }

  /*
   * Find products available for at least the requested quantity
   */
  @ApiOperation(value = "Find products available for at least the requested quantity")
  @GetMapping(value = END_OF_PATH_PRODUCT)
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
  @GetMapping(value = END_OF_PATH_PRODUCT + "/margins")
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
      LOGGER.info("Margins for the product " + product.getReference() + " = " + unitMargin + " , "
          + potentialMargin);
      productsMargins.add(new ProductMargin(product, unitMargin, potentialMargin));
    }
    if (productsMargins == null || productsMargins.isEmpty()) {
      throw new ProductNotFoundException();
    } else {
      return productsMargins;
    }
  }


  /*
   * Create a new product
   */
  @ApiOperation(value = "Create a new product in the inventory")
  @PostMapping(value = END_OF_PATH_PRODUCT)
  public ResponseEntity<Object> createProduct(@Valid @RequestBody Product product) {
    // Temporary URI
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      // verification of the request body
      if (product != null && product.getReference() != null) {
        // the product reference is an uppercase code
        product.setReference(product.getReference().toUpperCase().trim());
        uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{reference}")
            .buildAndExpand(product.getReference()).toUri();
        uri = microserviceControllerService.formatUriWithCorrectReference(uri, END_OF_PATH_PRODUCT,
            product.getReference());
        Product existingProduct = productDao.findByReference(product.getReference());
        // verifies that the product to be created is unique
        if (existingProduct == null) {
          Product newProduct = productDao.save(product);
          if (newProduct == null) {
            // creation failed
            return ResponseEntity.noContent().build();
          } else {
            // creation successful
            LOGGER.info(
                "Create product " + newProduct.getReference() + " with values = " + newProduct);
            uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{reference}")
                .buildAndExpand(newProduct.getReference()).toUri();
            MicroserviceResponseBody body = new MicroserviceResponseBody(
                HttpServletResponse.SC_CREATED, "Creation successful", uri, null, null, null);
            return ResponseEntity.created(uri).body(body);
          }
        } else {
          throw new ProductAlreadyExistsException(product.getReference());
        }
      } else {
        throw new ProductMandatoryReferenceException();
      }
    } catch (Exception ex) {
      return microserviceControllerService.buildResponseException(uri, ex);
    }
  }

  /*
   * Remove a product
   */
  @ApiOperation(value = "Remove a product from the inventory")
  @DeleteMapping(value = END_OF_PATH_PRODUCT + "/{reference}")
  public ResponseEntity<Object> deleteProduct(@PathVariable String reference) {
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      // verification of the request body
      if (reference != null && !reference.isBlank()) {
        // the product reference is an uppercase code
        reference = reference.toUpperCase().trim();
        uri = microserviceControllerService.formatUriWithCorrectReference(uri, END_OF_PATH_PRODUCT,
            reference);
        Product product = productDao.findByReference(reference);
        if (product != null) {
          LOGGER.info("Remove product " + reference + " with values = " + product);
          productDao.deleteById(product.getId());
          MicroserviceResponseBody body = new MicroserviceResponseBody(HttpServletResponse.SC_OK,
              "Deletion successful", uri, null, null, null);
          return ResponseEntity.ok().body(body);
        } else {
          throw new ProductNotFoundException(reference);
        }
      } else {
        throw new ProductMandatoryReferenceException();
      }
    } catch (Exception ex) {
      return microserviceControllerService.buildResponseException(uri, ex);
    }
  }

  /*
   * Change a product
   */
  @ApiOperation(value = "Modify all the attributes of a product of the inventory")
  @PutMapping(value = END_OF_PATH_PRODUCT + "/{reference}")
  public ResponseEntity<Object> updateProduct(@RequestBody Product product,
      @PathVariable String reference) {
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      // verification of the request body
      if (reference != null && !reference.isBlank()) {
        // the product reference is an uppercase code
        reference = reference.toUpperCase().trim();
        uri = microserviceControllerService.formatUriWithCorrectReference(uri, END_OF_PATH_PRODUCT,
            reference);
        Product existingProduct = productDao.findByReference(reference);
        if (existingProduct != null && product != null) {
          product.setId(existingProduct.getId());
          product.setReference(reference);
          LOGGER.info("Full update of product " + reference + " with values = " + product);
          productDao.save(product);
          MicroserviceResponseBody body = new MicroserviceResponseBody(HttpServletResponse.SC_OK,
              "Update successful", uri, null, null, null);
          return ResponseEntity.ok().body(body);
        } else {
          throw new ProductNotFoundException(reference);
        }
      } else {
        throw new ProductMandatoryReferenceException();
      }
    } catch (Exception ex) {
      return microserviceControllerService.buildResponseException(uri, ex);
    }
  }

  /*
   * Change partially a product
   */
  @ApiOperation(value = "Modify some attributes of a product of the inventory")
  @PatchMapping(value = END_OF_PATH_PRODUCT + "/{reference}")
  public ResponseEntity<Object> partialUpdateProduct(@RequestBody Product product,
      @PathVariable String reference) {
    // received path
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      // verification of the request body
      if (reference != null && !reference.isBlank()) {
        // the product reference is an uppercase code
        reference = reference.toUpperCase().trim();
        uri = microserviceControllerService.formatUriWithCorrectReference(uri, END_OF_PATH_PRODUCT,
            reference);
        Product existingProduct = productDao.findByReference(reference);
        if (existingProduct != null && product != null) {
          product.setId(existingProduct.getId());
          product.setReference(reference);
          LOGGER.info("Partial update of product " + reference + " with values = " + product);
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

          MicroserviceResponseBody body = new MicroserviceResponseBody(HttpServletResponse.SC_OK,
              "Partial update successful", uri, null, null, null);
          return ResponseEntity.ok().body(body);
        } else {
          throw new ProductNotFoundException(reference);
        }
      } else {
        throw new ProductMandatoryReferenceException();
      }
    } catch (Exception ex) {
      return microserviceControllerService.buildResponseException(uri, ex);
    }
  }
}
