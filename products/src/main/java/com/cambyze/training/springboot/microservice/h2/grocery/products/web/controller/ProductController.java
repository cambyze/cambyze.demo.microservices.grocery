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
import com.cambyze.commons.microservices.web.controller.MicroserviceControllerService;
import com.cambyze.commons.microservices.web.exceptions.RecordNotFoundException;
import com.cambyze.commons.tools.MathTools;
import com.cambyze.training.springboot.microservice.h2.grocery.products.dao.ProductDao;
import com.cambyze.training.springboot.microservice.h2.grocery.products.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * REST API controller for products management
 *
 * API documentation configuration and description in class SwaggerConfig
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@RestController
@Api(tags = {"ProductController"})
public class ProductController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
  private static final int NBDECIMALS = 2;
  private static final String PATH_PRODUCT = "/products";

  @Autowired
  private ProductDao productDao;

  @Autowired
  private MicroserviceControllerService microserviceControllerService;

  /**
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


  /**
   * Get product by its product reference
   * 
   * @param reference reference of the product to find *
   * @return a product with the reference
   */
  @ApiOperation(value = "Retrieve a product with its product reference")
  @GetMapping(value = PATH_PRODUCT + "/{reference}")
  public Product getProductbyReference(@PathVariable String reference) throws RuntimeException {
    Product searchProduct = new Product(reference);
    microserviceControllerService.prepareSearchingEntity(searchProduct);
    Product existingProduct = productDao.findByReference(searchProduct.getReference());
    microserviceControllerService.prepareSendingEntity(existingProduct, searchProduct);
    return existingProduct;
  }

  /**
   * Find products available for at least the requested quantity
   * 
   * @param quantityMin quantity minimum available for the product (it is an optional parameter) *
   * @return a list of products
   * 
   */
  @ApiOperation(value = "Find products available for at least the requested quantity")
  @GetMapping(value = PATH_PRODUCT)
  public List<Product> getProducts(
      @RequestParam(value = "quantityMin", defaultValue = "0", required = false) int quantityMin) {
    Product searchProduct = new Product();
    if (quantityMin < 0) {
      quantityMin = 0;
    }
    List<Product> products = productDao.findByAvailableGreaterThan(quantityMin);
    if (products == null || products.isEmpty()) {
      throw new RecordNotFoundException(searchProduct);
    } else {
      LOGGER.info(
          "Products avalaibles for the min quantity " + quantityMin + " = " + products.size());
      return products;
    }
  }


  /**
   * Calculate margin per product
   * 
   * @return list of products with the margin per product and the potential margin according to the
   *         stock
   */
  @ApiOperation(value = "Calculate margin per product")
  @GetMapping(value = PATH_PRODUCT + "/margins")
  public List<ProductMargin> getProductsMargins() {
    Product searchProduct = new Product();
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
      throw new RecordNotFoundException(searchProduct);
    } else {
      return productsMargins;
    }
  }


  /**
   * Create a new product
   * 
   * @param product the product to be created
   * @return a response body with information about the created product or errors when occurred
   */
  @ApiOperation(value = "Create a new product in the inventory")
  @PostMapping(value = PATH_PRODUCT)
  public ResponseEntity<Object> createProduct(@Valid @RequestBody Product product) {

    ResponseEntity<Object> ErrorResult =
        microserviceControllerService.prepareRequestEntityToPersist("", product,
            MicroserviceControllerService.OPERATION_CREATION);
    if (ErrorResult != null) {
      return ErrorResult;
    } else {

      Product existingProduct = productDao.findByReference(product.getReference());

      URI uri = microserviceControllerService.createTargetURI(product, PATH_PRODUCT);
      ErrorResult = microserviceControllerService.prepareEntityForUpdate(product, existingProduct,
          uri, MicroserviceControllerService.OPERATION_CREATION);
      if (ErrorResult != null) {
        return ErrorResult;
      } else {

        // creation of the product
        Product newProduct = productDao.save(product);

        return microserviceControllerService.createResponseBodyForUpdateSuccessful(newProduct, uri,
            MicroserviceControllerService.OPERATION_CREATION);
      }
    }
  }


  /**
   * Remove a product
   *
   * @param reference the reference of the product to be deleted
   * @return a response body with information about the removal action or errors when occurred
   */
  @ApiOperation(value = "Remove a product from the inventory")
  @DeleteMapping(value = PATH_PRODUCT + "/{reference}")
  public ResponseEntity<Object> deleteProduct(@PathVariable String reference) {

    Product product = new Product();
    product.setReference(reference);
    ResponseEntity<Object> ErrorResult =
        microserviceControllerService.prepareRequestEntityToPersist(reference, product,
            MicroserviceControllerService.OPERATION_SUPPRESSION);
    if (ErrorResult != null) {
      return ErrorResult;
    } else {

      // Search the product to update
      Product existingProduct = productDao.findByReference(product.getReference());

      URI uri = microserviceControllerService.createTargetURI(product, PATH_PRODUCT);
      ErrorResult = microserviceControllerService.prepareEntityForUpdate(product, existingProduct,
          uri, MicroserviceControllerService.OPERATION_SUPPRESSION);
      if (ErrorResult != null) {
        return ErrorResult;
      } else {

        // remove the product
        productDao.deleteById(product.getId());

        return microserviceControllerService.createResponseBodyForUpdateSuccessful(existingProduct,
            uri, MicroserviceControllerService.OPERATION_SUPPRESSION);

      }
    }
  }

  /**
   * Change a product
   * 
   * @param reference the reference of the product to be updated
   * @param product data to be updated
   * @return a response body with information about the modified product or errors when occurred
   */
  @ApiOperation(value = "Modify all the attributes of a product of the inventory")
  @PutMapping(value = PATH_PRODUCT + "/{reference}")
  public ResponseEntity<Object> updateProduct(@RequestBody Product product,
      @PathVariable String reference) {

    ResponseEntity<Object> ErrorResult =
        microserviceControllerService.prepareRequestEntityToPersist(reference, product,
            MicroserviceControllerService.OPERATION_FULL_UPDATE);
    if (ErrorResult != null) {
      return ErrorResult;
    } else {

      // Search the product to update
      Product existingProduct = productDao.findByReference(product.getReference());

      URI uri = microserviceControllerService.createTargetURI(product, PATH_PRODUCT);
      ErrorResult = microserviceControllerService.prepareEntityForUpdate(product, existingProduct,
          uri, MicroserviceControllerService.OPERATION_FULL_UPDATE);
      if (ErrorResult != null) {
        return ErrorResult;
      } else {

        // Save the modification
        productDao.save(product);

        return microserviceControllerService.createResponseBodyForUpdateSuccessful(existingProduct,
            uri, MicroserviceControllerService.OPERATION_FULL_UPDATE);
      }
    }
  }



  /**
   * Change partially a product
   * 
   * @param reference the reference of the product to be updated
   * @param product data to be updated
   * @return a response body with information about the modified product or errors when occurred
   */
  @ApiOperation(value = "Modify some attributes of a product of the inventory")
  @PatchMapping(value = PATH_PRODUCT + "/{reference}")
  public ResponseEntity<Object> partialUpdateProduct(@RequestBody Product product,
      @PathVariable String reference) {

    ResponseEntity<Object> ErrorResult =
        microserviceControllerService.prepareRequestEntityToPersist(reference, product,
            MicroserviceControllerService.OPERATION_PARTIAL_UPDATE);
    if (ErrorResult != null) {
      return ErrorResult;
    } else {

      // Search the product to update
      Product existingProduct = productDao.findByReference(product.getReference());

      URI uri = microserviceControllerService.createTargetURI(product, PATH_PRODUCT);
      ErrorResult = microserviceControllerService.prepareEntityForUpdate(product, existingProduct,
          uri, MicroserviceControllerService.OPERATION_PARTIAL_UPDATE);
      if (ErrorResult != null) {
        return ErrorResult;
      } else {

        // Update only modified values
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

        // Save the modification
        productDao.save(existingProduct);

        return microserviceControllerService.createResponseBodyForUpdateSuccessful(existingProduct,
            uri, MicroserviceControllerService.OPERATION_PARTIAL_UPDATE);
      }
    }
  }


}
