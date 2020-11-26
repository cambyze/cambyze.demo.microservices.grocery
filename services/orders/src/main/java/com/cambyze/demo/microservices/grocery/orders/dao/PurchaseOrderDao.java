package com.cambyze.demo.microservices.grocery.orders.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.cambyze.commons.microservices.dao.PersistEntityDao;
import com.cambyze.demo.microservices.grocery.orders.model.PurchaseOrder;

/**
 * DAO for orders entity
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-data/data-jpa/docs/2.3.2.RELEASE/reference/html/#jpa.query-methods.query-creation">Supported
 *      keywords inside method names</a>
 */
@Repository
public interface PurchaseOrderDao extends PersistEntityDao<PurchaseOrder> {

  /**
   * Find orders for a specific product
   * 
   * @param productReference reference of the product
   * @return list of purchase orders
   */
  List<PurchaseOrder> findByProductReference(String productReference);

  /**
   * Find orders paid or unpaid
   * 
   * @param paid flag indicates if the order is paid or not
   * @return list of purchase orders
   */
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
