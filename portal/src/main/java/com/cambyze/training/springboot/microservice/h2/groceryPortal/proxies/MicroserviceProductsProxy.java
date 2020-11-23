package com.cambyze.training.springboot.microservice.h2.groceryPortal.proxies;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.cambyze.training.springboot.microservice.h2.groceryPortal.beans.ProductBean;

/**
 * Proxy to interface with the microservices Products
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@FeignClient(name = "microservice-products", url = "http://localhost:9090")
public interface MicroserviceProductsProxy {

  public static final String PATH_PRODUCT = "/products";

  @GetMapping(value = PATH_PRODUCT)
  public List<ProductBean> getProducts();

  @GetMapping(value = PATH_PRODUCT + "/{reference}")
  public ProductBean getProductbyReference(@PathVariable("reference") String reference);
}
