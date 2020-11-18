package com.cambyze.training.springboot.microservice.h2.grocery.orders.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.cambyze.training.springboot.microservice.h2.grocery.orders.model.PurchaseOrder;

@Repository
public interface PurchaseOrderDao extends JpaRepository<PurchaseOrder, Long> {


  // JPA repositories mapping
  // https://docs.spring.io/spring-data/data-jpa/docs/2.3.2.RELEASE/reference/html/#jpa.query-methods.query-creation
  // to see Supported keywords inside method names

  PurchaseOrder findByReference(String reference);

  List<PurchaseOrder> findByProductReference(String productReference);

  List<PurchaseOrder> findByPaid(Boolean paid);

  /**
   * Find purchase id according to parameters
   * 
   * @param productReference reference of the product
   * @param paid indicates if the order is paid or not
   * @return list of purchase order id
   */
  @Query("SELECT id FROM PurchaseOrder "
      + "WHERE productReference = :productReference AND paid= :paid")
  List<Long> findByProductReferenceAndPaid(@Param("productReference") String productReference,
      @Param("paid") Boolean paid);

}
