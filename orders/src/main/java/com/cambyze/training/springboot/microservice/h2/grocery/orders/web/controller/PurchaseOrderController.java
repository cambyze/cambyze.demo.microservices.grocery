package com.cambyze.training.springboot.microservice.h2.grocery.orders.web.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import com.cambyze.commons.microservices.controller.MicroserviceControllerService;
import com.cambyze.commons.microservices.model.MicroserviceResponseBody;
import com.cambyze.training.springboot.microservice.h2.grocery.orders.dao.PurchaseOrderDao;
import com.cambyze.training.springboot.microservice.h2.grocery.orders.model.PurchaseOrder;
import com.cambyze.training.springboot.microservice.h2.grocery.orders.web.exceptions.PurchaseOrderAlreadyExistsException;
import com.cambyze.training.springboot.microservice.h2.grocery.orders.web.exceptions.PurchaseOrderMandatoryReferenceException;
import com.cambyze.training.springboot.microservice.h2.grocery.orders.web.exceptions.PurchaseOrderNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/*
 * REST API controller for purchase orders management
 *
 * API documentation configuration and description in class SwaggerConfig
 */
@RestController
@Api(tags = {"OrderController"})
public class PurchaseOrderController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderController.class);
  private static final String END_OF_PATH_ORDER = "/orders";

  @Autowired
  private PurchaseOrderDao orderDao;

  @Autowired
  private MicroserviceControllerService microserviceControllerService;

  /**
   * Get a purchase order by its order reference
   * 
   * @param reference reference of the purchase order to find *
   * @return a purchase order with the reference
   */
  @ApiOperation(value = "Retrieve a purchase order with its order reference")
  @GetMapping(value = END_OF_PATH_ORDER + "/{reference}")
  public PurchaseOrder getPurchaseOrderbyReference(@PathVariable String reference)
      throws RuntimeException {
    if (reference != null && !reference.isBlank()) {
      reference = reference.toUpperCase().trim();
      PurchaseOrder order = orderDao.findByReference(reference);
      if (order == null) {
        throw new PurchaseOrderNotFoundException(reference);
      } else {
        LOGGER.info("Order reference:" + reference + " = " + order);
        return order;
      }
    } else {
      throw new PurchaseOrderMandatoryReferenceException();
    }
  }

  /**
   * Find all the orders, order of a product and/or unpaid orders
   * 
   * @param productReference reference of a product ordered (optional parameter)
   * @param paid indicates if the order is paid or not (optional parameter)
   * @return a list of purchase orders
   */
  @ApiOperation(value = "Find all the orders, order of a product and/or unpaid orders")
  @GetMapping(value = END_OF_PATH_ORDER)
  public List<PurchaseOrder> getorders(
      @RequestParam(value = "productReference", required = false) String productReference,
      @RequestParam(value = "paid", required = false) Boolean paid) {
    List<PurchaseOrder> orders;
    if (productReference != null) {
      productReference = productReference.toUpperCase().trim();
    }

    if (productReference != null && !productReference.isBlank() && paid != null) {
      List<Long> orderIds = orderDao.findByProductReferenceAndPaid(productReference, paid);
      orders = new ArrayList<PurchaseOrder>();
      for (Long orderId : orderIds) {
        Optional<PurchaseOrder> order = orderDao.findById(orderId);
        if (order.isPresent()) {
          orders.add(order.get());
        }
      }
    } else if (productReference != null && !productReference.isBlank()) {
      orders = orderDao.findByProductReference(productReference);
    } else if (paid != null) {
      orders = orderDao.findByPaid(paid);
    } else {
      orders = orderDao.findAll();
    }

    if (orders == null || orders.isEmpty()) {
      throw new PurchaseOrderNotFoundException();
    } else {
      LOGGER.info("Order availables for the product reference " + productReference + " and paid = "
          + paid + " = " + orders.size());
      return orders;
    }
  }


  /**
   * Create a new product
   * 
   * @param product the product to be created
   * @return a response body with information about the created product or errors when occurred
   */
  @ApiOperation(value = "Create a new order of a product")
  @PostMapping(value = END_OF_PATH_ORDER)
  public ResponseEntity<Object> createOrder(@Valid @RequestBody PurchaseOrder purchaseOrder) {
    // Temporary URI
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      // verification of the request body
      if (purchaseOrder != null && purchaseOrder.getReference() != null) {
        // the order reference is an uppercase code
        purchaseOrder.setReference(purchaseOrder.getReference().toUpperCase().trim());
        uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{reference}")
            .buildAndExpand(purchaseOrder.getReference()).toUri();
        uri = microserviceControllerService.formatUriWithCorrectReference(uri, END_OF_PATH_ORDER,
            purchaseOrder.getReference());
        PurchaseOrder existingOrder = orderDao.findByReference(purchaseOrder.getReference());
        // verifies that the order to be created is unique
        if (existingOrder == null) {
          PurchaseOrder newOrder = orderDao.save(purchaseOrder);
          if (newOrder == null) {
            // creation failed
            return ResponseEntity.noContent().build();
          } else {
            // creation successful
            LOGGER.info("Create order " + newOrder.getReference() + " with values = " + newOrder);
            uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{reference}")
                .buildAndExpand(newOrder.getReference()).toUri();
            MicroserviceResponseBody body = new MicroserviceResponseBody(
                HttpServletResponse.SC_CREATED, "Creation successful", uri, null, null, null);
            return ResponseEntity.created(uri).body(body);
          }
        } else {
          throw new PurchaseOrderAlreadyExistsException(purchaseOrder.getReference());
        }
      } else {
        throw new PurchaseOrderMandatoryReferenceException();
      }
    } catch (Exception ex) {
      return microserviceControllerService.buildResponseException(uri, ex);
    }
  }

  /**
   * Remove a purchase order
   *
   * @param reference the reference of the order to be deleted
   * @return a response body with information about the removal action or errors when occurred
   */
  @ApiOperation(value = "Remove a purchase order")
  @DeleteMapping(value = END_OF_PATH_ORDER + "/{reference}")
  public ResponseEntity<Object> deleteProduct(@PathVariable String reference) {
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      // verification of the request body
      if (reference != null && !reference.isBlank()) {
        // the order reference is an uppercase code
        reference = reference.toUpperCase().trim();
        uri = microserviceControllerService.formatUriWithCorrectReference(uri, END_OF_PATH_ORDER,
            reference);
        PurchaseOrder order = orderDao.findByReference(reference);
        if (order != null) {
          LOGGER.info("Remove order " + reference + " with values = " + order);
          orderDao.deleteById(order.getId());
          MicroserviceResponseBody body = new MicroserviceResponseBody(HttpServletResponse.SC_OK,
              "Deletion successful", uri, null, null, null);
          return ResponseEntity.ok().body(body);
        } else {
          throw new PurchaseOrderNotFoundException(reference);
        }
      } else {
        throw new PurchaseOrderMandatoryReferenceException();
      }
    } catch (Exception ex) {
      return microserviceControllerService.buildResponseException(uri, ex);
    }
  }

  /**
   * Change a purchase order
   * 
   * @param reference the reference of the order to be updated
   * @param purchaseOrder data to be updated
   * @return a response body with information about the modified order or errors when occurred
   */
  @ApiOperation(value = "Modify all the attributes of a product of the inventory")
  @PutMapping(value = END_OF_PATH_ORDER + "/{reference}")
  public ResponseEntity<Object> updateProduct(@RequestBody PurchaseOrder purchaseOrder,
      @PathVariable String reference) {
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      // verification of the request body
      if (reference != null && !reference.isBlank()) {
        // the order reference is an uppercase code
        reference = reference.toUpperCase().trim();
        uri = microserviceControllerService.formatUriWithCorrectReference(uri, END_OF_PATH_ORDER,
            reference);
        PurchaseOrder existingOrder = orderDao.findByReference(reference);
        if (existingOrder != null && purchaseOrder != null) {
          purchaseOrder.setId(existingOrder.getId());
          purchaseOrder.setReference(existingOrder.getReference());
          LOGGER.info("Full update of order " + reference + " with values = " + purchaseOrder);
          orderDao.save(purchaseOrder);
          MicroserviceResponseBody body = new MicroserviceResponseBody(HttpServletResponse.SC_OK,
              "Update successful", uri, null, null, null);
          return ResponseEntity.ok().body(body);
        } else {
          throw new PurchaseOrderNotFoundException(reference);
        }
      } else {
        throw new PurchaseOrderMandatoryReferenceException();
      }
    } catch (Exception ex) {
      return microserviceControllerService.buildResponseException(uri, ex);
    }
  }

  /**
   * Change partially a purchase order
   * 
   * @param reference the reference of the order to be updated
   * @param product data to be updated
   * @return a response body with information about the modified product or errors when occurred
   */
  @ApiOperation(value = "Modify some attributes of a product of the inventory")
  @PatchMapping(value = END_OF_PATH_ORDER + "/{reference}")
  public ResponseEntity<Object> partialUpdateProduct(@RequestBody PurchaseOrder purchaseOrder,
      @PathVariable String reference) {
    // received path
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
    try {
      // verification of the request body
      if (reference != null && !reference.isBlank()) {
        // the order reference is an uppercase code
        reference = reference.toUpperCase().trim();
        uri = microserviceControllerService.formatUriWithCorrectReference(uri, END_OF_PATH_ORDER,
            reference);
        PurchaseOrder existingOrder = orderDao.findByReference(reference);
        if (existingOrder != null && purchaseOrder != null) {
          purchaseOrder.setId(existingOrder.getId());
          purchaseOrder.setReference(existingOrder.getReference());
          LOGGER.info("Partial update of order " + reference + " with values = " + purchaseOrder);
          if (purchaseOrder.getPaid() != null) {
            existingOrder.setPaid(purchaseOrder.getPaid());
          }
          if (purchaseOrder.getOrderDate() != null) {
            existingOrder.setOrderDate(purchaseOrder.getOrderDate());
          }
          if (purchaseOrder.getProductReference() != null) {
            existingOrder.setProductReference(purchaseOrder.getProductReference());
          }
          if (purchaseOrder.getQuantity() != null) {
            existingOrder.setQuantity(purchaseOrder.getQuantity());
          }
          orderDao.save(existingOrder);

          MicroserviceResponseBody body = new MicroserviceResponseBody(HttpServletResponse.SC_OK,
              "Partial update successful", uri, null, null, null);
          return ResponseEntity.ok().body(body);
        } else {
          throw new PurchaseOrderNotFoundException(reference);
        }
      } else {
        throw new PurchaseOrderMandatoryReferenceException();
      }
    } catch (Exception ex) {
      return microserviceControllerService.buildResponseException(uri, ex);
    }
  }
}
