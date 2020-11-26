package com.cambyze.demo.microservices.grocery.payments.web.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cambyze.commons.microservices.web.controller.MicroserviceControllerService;
import com.cambyze.commons.microservices.web.exceptions.RecordNotFoundException;
import com.cambyze.demo.microservices.grocery.payments.dao.PaymentDao;
import com.cambyze.demo.microservices.grocery.payments.model.Payment;
import com.cambyze.demo.microservices.grocery.payments.model.PaymentStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * REST API controller for payments management
 *
 * API documentation configuration and description in class SwaggerConfig
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@RestController
@Api(tags = {"PaymentController"})
public class PaymentController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);
  private static final String PATH_PAYMENT = "/payments";
  private static final String CANCELATION_METHOD = "/cancelation";


  @Autowired
  private PaymentDao paymentDao;

  @Autowired
  private MicroserviceControllerService microserviceControllerService;

  /**
   * Get a payment by its reference
   * 
   * @param reference reference of the payment to find
   * @return the payment
   */
  @ApiOperation(value = "Retrieve a payment with its reference")
  @GetMapping(value = PATH_PAYMENT + "/{reference}")
  public Payment getPaymentByReference(@PathVariable String reference) throws RuntimeException {
    Payment searchPayment = new Payment(reference);
    microserviceControllerService.prepareSearchingEntity(searchPayment);
    Payment existingOrder = paymentDao.findByReference(searchPayment.getReference());
    microserviceControllerService.prepareSendingEntity(existingOrder, searchPayment);
    return existingOrder;
  }


  /**
   * Find all the payments, payments of a purchase order and/or payment status
   * 
   * @param orderReference reference of a product ordered (optional parameter)
   * @param status status of the payment (optional parameter)
   * @return a list of purchase orders
   */
  @ApiOperation(
      value = "Find all the payments, payments of a purchase order and/or masked card number")
  @GetMapping(value = PATH_PAYMENT)
  public List<Payment> getPayments(
      @RequestParam(value = "orderReference", required = false) String orderReference,
      @RequestParam(value = "status", required = false) PaymentStatus status) {
    List<Payment> payments;

    if (orderReference != null) {
      orderReference = orderReference.toUpperCase().trim();
    }

    if (orderReference != null && !orderReference.isBlank() && status != null) {
      List<Long> paymentIds = paymentDao.findByOrderReferenceAndStatus(orderReference, status);
      payments = new ArrayList<Payment>();
      for (Long paymentId : paymentIds) {
        Optional<Payment> payment = paymentDao.findById(paymentId);
        if (payment.isPresent()) {
          payments.add(payment.get());
        }
      }
    } else if (orderReference != null && !orderReference.isBlank()) {
      payments = paymentDao.findByOrderReference(orderReference);
    } else if (status != null) {
      payments = paymentDao.findByStatus(status);
    } else {
      payments = paymentDao.findAll();
    }

    if (payments == null || payments.isEmpty()) {
      Payment searchPayment = new Payment();
      throw new RecordNotFoundException(searchPayment);
    } else {
      LOGGER.info("Payments availables for the order reference " + orderReference + " and status = "
          + status + " = " + payments.size());
      return payments;
    }
  }


  /**
   * Process a payment for a purchase order
   * 
   * @param payment the payment to be created
   * @return a response body with information about the created payment or errors when occurred
   */
  @ApiOperation(value = "Create a new order of a product")
  @PostMapping(value = PATH_PAYMENT)
  public ResponseEntity<Object> payOrder(@Valid @RequestBody Payment payment) {

    ResponseEntity<Object> ErrorResult = microserviceControllerService
        .prepareRequestEntityToPersist("", payment, MicroserviceControllerService.OPERATION_CREATE);
    if (ErrorResult != null) {
      return ErrorResult;
    } else {

      // Verify if the payment does not already exist
      Payment existingPayment = paymentDao.findByReference(payment.getReference());

      URI uri = microserviceControllerService.createTargetURI(payment, PATH_PAYMENT);
      ErrorResult = microserviceControllerService.prepareEntityForCUD(payment, existingPayment, uri,
          MicroserviceControllerService.OPERATION_CREATE);
      if (ErrorResult != null) {
        return ErrorResult;
      } else {

        // The payment is created as valid
        payment.setStatus(PaymentStatus.VALID);
        // creation of the payment
        Payment newPayment = paymentDao.save(payment);


        return microserviceControllerService.createResponseBodyCUDSuccessful(newPayment, uri,
            MicroserviceControllerService.OPERATION_CREATE);
      }
    }
  }


  /**
   * Cancel a payment
   * 
   * @param reference the reference of payment to cancel
   * @return a response body with information about the cancelled payment or errors when occurred
   */
  @ApiOperation(value = "Cancel a payment")
  @PostMapping(value = PATH_PAYMENT + "/{reference}" + CANCELATION_METHOD)
  public ResponseEntity<Object> cancelPayment(@PathVariable String reference) {

    Payment payment = new Payment(reference);
    ResponseEntity<Object> ErrorResult =
        microserviceControllerService.prepareRequestEntityToPersist(reference, payment,
            MicroserviceControllerService.OPERATION_OTHERS);
    if (ErrorResult != null) {
      return ErrorResult;
    } else {

      // Search the payment to cancel
      Payment existingPayment = paymentDao.findByReference(payment.getReference());

      URI uri = microserviceControllerService.createTargetURI(payment, PATH_PAYMENT);
      ErrorResult = microserviceControllerService.prepareEntityForCUD(payment, existingPayment, uri,
          MicroserviceControllerService.OPERATION_OTHERS);
      if (ErrorResult != null) {
        return ErrorResult;
      } else {

        existingPayment.setStatus(PaymentStatus.CANCELLED);
        // Save the modification
        paymentDao.save(existingPayment);

        return microserviceControllerService.createResponseBodyCUDSuccessful(existingPayment, uri,
            MicroserviceControllerService.OPERATION_OTHERS);
      }
    }
  }
}
