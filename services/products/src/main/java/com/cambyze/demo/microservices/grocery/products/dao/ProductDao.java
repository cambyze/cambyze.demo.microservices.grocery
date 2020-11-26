package com.cambyze.demo.microservices.grocery.products.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.cambyze.commons.microservices.dao.PersistEntityDao;
import com.cambyze.demo.microservices.grocery.products.model.Product;

/**
 * DAO for products entity
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-data/data-jpa/docs/2.3.2.RELEASE/reference/html/#jpa.query-methods.query-creation">Supported
 *      keywords inside method names</a>
 */
@Repository
public interface ProductDao extends PersistEntityDao<Product> {

  /**
   * find products with a stock available
   * 
   * @param quantity minimum quantity in the stock
   * @return list of products
   */
  List<Product> findByAvailableGreaterThan(int quantity);

  /**
   * find products with quantity and price criteria
   * 
   * @param priceMax price maximum for the product
   * @param minQuantity minimum quantity in the stock
   * @return list of products
   */
  @Query("SELECT id, reference, name, imageURL, price, purchasePrice, available FROM Product "
      + "WHERE price <= :priceMax AND available >= :minQuantity")
  List<Product> findByMaxPriceAndMinQuantity(@Param("priceMax") double priceMax,
      @Param("minQuantity") int minQuantity);

}
