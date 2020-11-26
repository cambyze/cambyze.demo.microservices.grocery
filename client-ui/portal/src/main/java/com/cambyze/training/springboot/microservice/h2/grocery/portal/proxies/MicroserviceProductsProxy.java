package com.cambyze.training.springboot.microservice.h2.grocery.portal.proxies;

import java.util.List;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.cambyze.training.springboot.microservice.h2.grocery.portal.beans.ProductBean;

/**
 * Proxy to interface with the microservices Products
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@FeignClient(name = "training-products")
@RibbonClient(name = "training-products")
public interface MicroserviceProductsProxy {

  public static final String PATH_PRODUCT = "/products";

  @GetMapping(value = PATH_PRODUCT)
  public List<ProductBean> getProducts();

  @GetMapping(value = PATH_PRODUCT + "/{reference}")
  public ProductBean getProductbyReference(@PathVariable("reference") String reference);
}
