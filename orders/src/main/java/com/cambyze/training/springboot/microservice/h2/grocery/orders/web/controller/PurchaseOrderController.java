package com.cambyze.training.springboot.microservice.h2.grocery.orders.web.controller;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.cambyze.training.springboot.microservice.h2.grocery.orders.dao.PurchaseOrderDao;
import com.cambyze.training.springboot.microservice.h2.grocery.orders.model.PurchaseOrder;
import com.cambyze.training.springboot.microservice.h2.grocery.products.web.exceptions.PurchaseOrderNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/*
 * REST API controller for products management
 *
 * API documentation configuration and description in class SwaggerConfig
 */
@RestController
@Api(tags = {"OrderController"})
public class PurchaseOrderController {

  @Autowired
  private PurchaseOrderDao orderDao;


  /*
   * Get order by id
   */
  @ApiOperation(value = "Retrieve an order with its id")
  @GetMapping(value = "/orders/{id}")
  public PurchaseOrder getOrderbyId(@PathVariable long id) throws RuntimeException {
    PurchaseOrder order = orderDao.findById(id);
    if (order == null) {
      throw new PurchaseOrderNotFoundException(id);
    } else {
      return order;
    }
  }

  /*
   * Find all the orders, order of a product or unpaid orders
   */
  @ApiOperation(value = "Find all the orders, order of a product or unpaid orders")
  @GetMapping(value = "/orders")
  public List<PurchaseOrder> getorders(@Valid @RequestBody PurchaseOrder order) {
    List<PurchaseOrder> orders;
    if (order == null) {
      order = new PurchaseOrder();
    } ;

    if (order.getPaid() != null & order.getPaid() == false) {
      orders = orderDao.findByPaid(false);
    } else if (order.getProductReference() > 0) {
      orders = orderDao.findByProductReference(order.getProductReference());
    } else {
      orders = orderDao.findAll();
    }


    if (orders == null || orders.isEmpty()) {
      throw new PurchaseOrderNotFoundException();
    } else {
      return orders;
    }
  }


  @ApiOperation(value = "Create a new order of a product")
  @PostMapping(value = "/orders")
  public ResponseEntity<Object> createOrder(@Valid @RequestBody PurchaseOrder order) {
    PurchaseOrder newOrder = orderDao.save(order);
    if (newOrder == null) {
      return ResponseEntity.noContent().build();
    } else {
      URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
          .buildAndExpand(newOrder.getId()).toUri();
      return ResponseEntity.created(uri).build();
    }
  }

  @ApiOperation(value = "Remove an order from the inventory")
  @DeleteMapping(value = "/orders/{id}")
  public void deleteOrder(@PathVariable long id) {
    orderDao.deleteById(id);
  }


}
