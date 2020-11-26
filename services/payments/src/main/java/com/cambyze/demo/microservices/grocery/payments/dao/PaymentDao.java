package com.cambyze.demo.microservices.grocery.payments.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.cambyze.commons.microservices.dao.PersistEntityDao;
import com.cambyze.demo.microservices.grocery.payments.model.Payment;
import com.cambyze.demo.microservices.grocery.payments.model.PaymentStatus;

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
public interface PaymentDao extends PersistEntityDao<Payment> {

  /**
   * Find payments for a specific order
   * 
   * @param orderReference reference of the purchase order
   * @return list of payments
   */
  List<Payment> findByOrderReference(String orderReference);


  /**
   * Find payments for a specific card
   * 
   * @param maskedCardNumber card number with mask for security reason
   * @return list of payments
   */
  List<Payment> findByMaskedCardNumber(String maskedCardNumber);

  /**
   * Find payments for a specific status
   * 
   * @param status status of the payment
   * @return list of payments
   */
  List<Payment> findByStatus(PaymentStatus status);

  /**
   * Find payments id according to parameters
   * 
   * @param orderReference reference of the product
   * @param status status of the payment
   * @return list of payments
   */
  @Query("SELECT id FROM Payment WHERE orderReference = :orderReference AND status = :status")
  List<Long> findByOrderReferenceAndStatus(@Param("orderReference") String orderReference,
      @Param("status") PaymentStatus status);
}
