package com.cambyze.training.springboot.microservice.h2.grocery.orders.web.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import com.cambyze.training.springboot.microservice.h2.grocery.orders.dao.PurchaseOrderDao;
import com.cambyze.training.springboot.microservice.h2.grocery.orders.model.PurchaseOrder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * REST API controller for purchase orders management
 *
 * API documentation configuration and description in class SwaggerConfig
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@RestController
@Api(tags = {"OrderController"})
public class PurchaseOrderController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderController.class);
  private static final String PATH_ORDER = "/orders";

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
  @GetMapping(value = PATH_ORDER + "/{reference}")
  public PurchaseOrder getOrderbyReference(@PathVariable String reference) throws RuntimeException {
    PurchaseOrder searchOrder = new PurchaseOrder(reference);
    microserviceControllerService.prepareSearchingEntity(searchOrder);
    PurchaseOrder existingOrder = orderDao.findByReference(searchOrder.getReference());
    microserviceControllerService.prepareSendingEntity(existingOrder, searchOrder);
    return existingOrder;
  }

  /**
   * Find all the orders, order of a product and/or unpaid orders
   * 
   * @param productReference reference of a product ordered (optional parameter)
   * @param paid indicates if the order is paid or not (optional parameter)
   * @return a list of purchase orders
   */
  @ApiOperation(value = "Find all the orders, order of a product and/or unpaid orders")
  @GetMapping(value = PATH_ORDER)
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
      PurchaseOrder searchOrder = new PurchaseOrder();
      throw new RecordNotFoundException(searchOrder);
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
  @PostMapping(value = PATH_ORDER)
  public ResponseEntity<Object> createOrder(@Valid @RequestBody PurchaseOrder purchaseOrder) {

    ResponseEntity<Object> ErrorResult =
        microserviceControllerService.prepareRequestEntityToPersist("", purchaseOrder,
            MicroserviceControllerService.OPERATION_CREATE);
    if (ErrorResult != null) {
      return ErrorResult;
    } else {

      // Verify if the order does not already exist
      PurchaseOrder existingOrder = orderDao.findByReference(purchaseOrder.getReference());

      URI uri = microserviceControllerService.createTargetURI(purchaseOrder, PATH_ORDER);
      ErrorResult = microserviceControllerService.prepareEntityForCUD(purchaseOrder, existingOrder,
          uri, MicroserviceControllerService.OPERATION_CREATE);
      if (ErrorResult != null) {
        return ErrorResult;
      } else {

        // creation of the product
        PurchaseOrder newOrder = orderDao.save(purchaseOrder);

        return microserviceControllerService.createResponseBodyCUDSuccessful(newOrder, uri,
            MicroserviceControllerService.OPERATION_CREATE);
      }
    }
  }

  /**
   * Remove a purchase order
   *
   * @param reference the reference of the order to be deleted
   * @return a response body with information about the removal action or errors when occurred
   */
  @ApiOperation(value = "Remove a purchase order")
  @DeleteMapping(value = PATH_ORDER + "/{reference}")
  public ResponseEntity<Object> deleteProduct(@PathVariable String reference) {
    PurchaseOrder order = new PurchaseOrder(reference);
    ResponseEntity<Object> ErrorResult =
        microserviceControllerService.prepareRequestEntityToPersist(reference, order,
            MicroserviceControllerService.OPERATION_DELETE);
    if (ErrorResult != null) {
      return ErrorResult;
    } else {

      // Search the product to remove
      PurchaseOrder existingOrder = orderDao.findByReference(order.getReference());

      URI uri = microserviceControllerService.createTargetURI(order, PATH_ORDER);
      ErrorResult = microserviceControllerService.prepareEntityForCUD(order, existingOrder, uri,
          MicroserviceControllerService.OPERATION_DELETE);
      if (ErrorResult != null) {
        return ErrorResult;
      } else {

        // remove the order
        orderDao.deleteById(order.getId());

        return microserviceControllerService.createResponseBodyCUDSuccessful(existingOrder, uri,
            MicroserviceControllerService.OPERATION_DELETE);

      }
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
  @PutMapping(value = PATH_ORDER + "/{reference}")
  public ResponseEntity<Object> updateProduct(@RequestBody PurchaseOrder purchaseOrder,
      @PathVariable String reference) {

    ResponseEntity<Object> ErrorResult =
        microserviceControllerService.prepareRequestEntityToPersist(reference, purchaseOrder,
            MicroserviceControllerService.OPERATION_FULL_UPDATE);
    if (ErrorResult != null) {
      return ErrorResult;
    } else {

      // Search the order to update
      PurchaseOrder existingOrder = orderDao.findByReference(purchaseOrder.getReference());

      URI uri = microserviceControllerService.createTargetURI(purchaseOrder, PATH_ORDER);
      ErrorResult = microserviceControllerService.prepareEntityForCUD(purchaseOrder, existingOrder,
          uri, MicroserviceControllerService.OPERATION_FULL_UPDATE);
      if (ErrorResult != null) {
        return ErrorResult;
      } else {

        // Save the modification
        orderDao.save(purchaseOrder);

        return microserviceControllerService.createResponseBodyCUDSuccessful(purchaseOrder, uri,
            MicroserviceControllerService.OPERATION_FULL_UPDATE);
      }
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
  @PatchMapping(value = PATH_ORDER + "/{reference}")
  public ResponseEntity<Object> partialUpdateProduct(@RequestBody PurchaseOrder purchaseOrder,
      @PathVariable String reference) {

    ResponseEntity<Object> ErrorResult =
        microserviceControllerService.prepareRequestEntityToPersist(reference, purchaseOrder,
            MicroserviceControllerService.OPERATION_PARTIAL_UPDATE);
    if (ErrorResult != null) {
      return ErrorResult;
    } else {

      // Search the order to update
      PurchaseOrder existingOrder = orderDao.findByReference(purchaseOrder.getReference());

      URI uri = microserviceControllerService.createTargetURI(purchaseOrder, PATH_ORDER);
      ErrorResult = microserviceControllerService.prepareEntityForCUD(purchaseOrder, existingOrder,
          uri, MicroserviceControllerService.OPERATION_PARTIAL_UPDATE);
      if (ErrorResult != null) {
        return ErrorResult;
      } else {

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
        // Save the modification
        orderDao.save(existingOrder);

        return microserviceControllerService.createResponseBodyCUDSuccessful(existingOrder, uri,
            MicroserviceControllerService.OPERATION_PARTIAL_UPDATE);
      }
    }
  }
}
