package com.cambyze.training.springboot.microservice.h2.grocery.orders.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cambyze.training.springboot.microservice.h2.grocery.orders.model.PurchaseOrder;

@Repository
public interface PurchaseOrderDao extends JpaRepository<PurchaseOrder, Long> {


  // JPA repositories mapping
  // https://docs.spring.io/spring-data/data-jpa/docs/2.3.2.RELEASE/reference/html/#jpa.query-methods.query-creation
  // to see Supported keywords inside method names

  PurchaseOrder findById(long id);

  List<PurchaseOrder> findByProductReference(long productReference);

  List<PurchaseOrder> findByPaid(Boolean paid);


}
