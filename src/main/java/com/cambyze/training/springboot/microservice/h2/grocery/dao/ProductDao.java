package com.cambyze.training.springboot.microservice.h2.grocery.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.cambyze.training.springboot.microservice.h2.grocery.model.Product;

@Repository
public interface ProductDao extends JpaRepository<Product, Long> {


  // JPA repositories mapping
  // https://docs.spring.io/spring-data/data-jpa/docs/2.3.2.RELEASE/reference/html/#jpa.query-methods.query-creation
  // to see Supported keywords inside method names

  Product findById(long id);

  List<Product> findByAvailableGreaterThan(int quantity);

  @Query("SELECT id,name,price,available FROM Product WHERE price <= :priceMax AND available >= :minQuantity")
  List<Product> findByMaxPriceAndMinQuantity(@Param("priceMax") double priceMax,
      @Param("minQuantity") int minQuantity);

}
