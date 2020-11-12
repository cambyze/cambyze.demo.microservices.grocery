package com.cambyze.training.springboot.microservice.h2.grocery.products.web.controller;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

  /*
   * Response body for microservices
   */
  public class MicroserviceResponseBody {

    private int status;
    private String timestamp;
    private String message;
    private String error;
    private String exception;
    private URI path;

    private List<microserviceResponseError> errors;

    public MicroserviceResponseBody() {
      super();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss*SSSZZZZ");
      this.timestamp = dateFormat.format(Calendar.getInstance().getTime());
      this.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }


    public MicroserviceResponseBody(int status, String message, URI path, String error,
        String exception, List<microserviceResponseError> errors) {
      super();
      this.status = status;
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss*SSSZZZZ");
      this.timestamp = dateFormat.format(Calendar.getInstance().getTime());
      this.message = message;
      this.path = path;
      this.error = error;
      this.exception = exception;
      this.errors = errors;
    }

    public int getStatus() {
      return status;
    }

    public void setStatus(int status) {
      this.status = status;
    }

    public String getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(String timestamp) {
      this.timestamp = timestamp;
    }

    public URI getPath() {
      return path;
    }

    public void setPath(URI path) {
      this.path = path;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getError() {
      return error;
    }


    public void setError(String error) {
      this.error = error;
    }


    public String getException() {
      return exception;
    }


    public void setException(String exception) {
      this.exception = exception;
    }


    public List<microserviceResponseError> getErrors() {
      return errors;
    }

    public void setErrors(List<microserviceResponseError> errors) {
      this.errors = errors;
    }
  }

  /*
   * Error information in case of microservices errors
   */
  public class microserviceResponseError {
    String message;
    String exception;

    public microserviceResponseError() {
      super();
    }

    public microserviceResponseError(String message, String exception) {
      super();
      this.message = message;
      this.exception = exception;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getException() {
      return exception;
    }

    public void setException(String exception) {
      this.exception = exception;
    }

  }


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
   * Analyse exception in order to build a response body for the microservices with errors
   */
  private ResponseEntity<Object> buildResponseException(URI path, Exception exception) {

    // Initialisation
    MicroserviceResponseBody microserviceResponseBody = new MicroserviceResponseBody();
    microserviceResponseBody.setPath(path);
    // Default status
    microserviceResponseBody.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    ArrayList<microserviceResponseError> errors = new ArrayList<microserviceResponseError>();
    microserviceResponseBody.setErrors(errors);

    // Exception analysis
    updateMicroserviceResponseBodyWithException(microserviceResponseBody, exception, null);

    // Determine the error message according to the HTTP status
    switch (microserviceResponseBody.getStatus()) {
      case HttpServletResponse.SC_NOT_FOUND:
        microserviceResponseBody.setError(HttpStatus.NOT_FOUND.toString());
        break;
      case HttpServletResponse.SC_BAD_REQUEST:
        microserviceResponseBody.setError(HttpStatus.BAD_REQUEST.toString());
        break;
      case HttpServletResponse.SC_NO_CONTENT:
        microserviceResponseBody.setError(HttpStatus.NO_CONTENT.toString());
        break;
      default:
        microserviceResponseBody.setError(HttpStatus.INTERNAL_SERVER_ERROR.toString());
    }
    return ResponseEntity.status(microserviceResponseBody.getStatus())
        .body(microserviceResponseBody);
  }

  /*
   * Recursively analyse the exception and cause in order to fill the microservices response body in
   * case of error
   */
  private void updateMicroserviceResponseBodyWithException(
      MicroserviceResponseBody microserviceResponseBody, Throwable throwable, Throwable parent) {
    if (throwable != null) {
      LOGGER.error(throwable.getMessage());

      // Force status NOT_FOUND
      if (throwable instanceof ProductNotFoundException) {
        microserviceResponseBody.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }

      // First call
      if (parent == null) {
        microserviceResponseBody.setMessage(throwable.getMessage());
        microserviceResponseBody.setException(throwable.getClass().getName());
        microserviceResponseBody.getErrors().add(
            new microserviceResponseError(throwable.getMessage(), throwable.getClass().getName()));
      }
      // Recursive calls with exception different that the parent to prevent duplicated values
      else if (throwable.getClass() != null && throwable.getMessage() != null
          && parent.getClass() != null
          && !throwable.getClass().getName().equalsIgnoreCase(parent.getClass().getName())) {
        // Add new error
        microserviceResponseBody.getErrors().add(
            new microserviceResponseError(throwable.getMessage(), throwable.getClass().getName()));

        // Management of the Hibernate constraint violation
        if (throwable instanceof ConstraintViolationException) {
          ConstraintViolationException constraintViolationException =
              (ConstraintViolationException) throwable;
          Set<ConstraintViolation<?>> constraintViolations =
              constraintViolationException.getConstraintViolations();
          if (constraintViolations != null && !constraintViolations.isEmpty()) {
            for (ConstraintViolation<?> constraintViolation : constraintViolations) {
              if (constraintViolation != null) {
                LOGGER.error(constraintViolation.getMessage());

                // Data validation error = BAD_REQUEST
                // Construction of the final message
                if (constraintViolation.getPropertyPath() != null
                    && constraintViolation.getMessage() != null)
                  microserviceResponseBody.setMessage("'" + constraintViolation.getPropertyPath()
                      + "': " + constraintViolation.getMessage().toString());
                microserviceResponseBody.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                microserviceResponseBody.setException(throwable.getClass().getName());
              }
            }
          }
        }
      }

      // Recursive call
      updateMicroserviceResponseBodyWithException(microserviceResponseBody, throwable.getCause(),
          throwable);
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


  /*
   * Create a new product
   */
  @ApiOperation(value = "Create a new product in the inventory")
  @PostMapping(value = "/products")
  public ResponseEntity<Object> createProduct(@Valid @RequestBody Product product) {
    // Temporary URI
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      Product newProduct = productDao.save(product);
      if (newProduct == null) {
        return ResponseEntity.noContent().build();
      } else {
        LOGGER.info("Create product " + newProduct.getCode() + " with values = " + newProduct);
        uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{code}")
            .buildAndExpand(newProduct.getCode()).toUri();
        MicroserviceResponseBody body = new MicroserviceResponseBody(HttpServletResponse.SC_CREATED,
            "Creation successful", uri, null, null, null);
        return ResponseEntity.created(uri).body(body);
      }
    } catch (Exception ex) {
      return buildResponseException(uri, ex);
    }
  }

  /*
   * Remove a product
   */
  @ApiOperation(value = "Remove a product from the inventory")
  @DeleteMapping(value = "/products/{code}")
  public ResponseEntity<Object> deleteProduct(@PathVariable String code) {
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      Product product = productDao.findByCode(code);
      if (product != null) {
        LOGGER.info("Remove product " + code + " with values = " + product);
        productDao.deleteById(product.getId());
        MicroserviceResponseBody body = new MicroserviceResponseBody(HttpServletResponse.SC_OK,
            "Deletion successful", uri, null, null, null);
        return ResponseEntity.ok().body(body);
      } else {
        throw new ProductNotFoundException(code);
      }
    } catch (Exception ex) {
      return buildResponseException(uri, ex);
    }
  }

  /*
   * Change a product
   */
  @ApiOperation(value = "Modify all the attributes of a product of the inventory")
  @PutMapping(value = "/products/{code}")
  public ResponseEntity<Object> updateProduct(@RequestBody Product product,
      @PathVariable String code) {
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      Product existingProduct = productDao.findByCode(code);
      if (existingProduct != null && product != null) {
        product.setId(existingProduct.getId());
        product.setCode(code);
        LOGGER.info("Full update of product " + code + " with values = " + product);
        productDao.save(product);
        MicroserviceResponseBody body = new MicroserviceResponseBody(HttpServletResponse.SC_OK,
            "Update successful", uri, null, null, null);
        return ResponseEntity.ok().body(body);
      } else {
        throw new ProductNotFoundException(code);
      }
    } catch (Exception ex) {
      return buildResponseException(uri, ex);
    }
  }

  /*
   * Change partially a product
   */
  @ApiOperation(value = "Modify some attributes of a product of the inventory")
  @PatchMapping(value = "/products/{code}")
  public ResponseEntity<Object> partialUpdateProduct(@RequestBody Product product,
      @PathVariable String code) {
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      Product existingProduct = productDao.findByCode(code);
      if (existingProduct != null && product != null) {
        product.setId(existingProduct.getId());
        product.setCode(code);
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

        MicroserviceResponseBody body = new MicroserviceResponseBody(HttpServletResponse.SC_OK,
            "Partial update successful", uri, null, null, null);
        return ResponseEntity.ok().body(body);
      } else {
        throw new ProductNotFoundException(code);
      }
    } catch (Exception ex) {
      return buildResponseException(uri, ex);
    }
  }
}
