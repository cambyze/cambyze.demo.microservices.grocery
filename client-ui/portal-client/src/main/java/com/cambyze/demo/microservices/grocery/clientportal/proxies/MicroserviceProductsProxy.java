package com.cambyze.demo.microservices.grocery.clientportal.proxies;

import java.util.List;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.cambyze.demo.microservices.grocery.clientportal.beans.ProductBean;

/**
 * Proxy to interface with the microservices Products
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@FeignClient(name = "demo-gateway-server")
@RibbonClient(name = "demo-products")
public interface MicroserviceProductsProxy {

  public static final String PATH_PRODUCT = "/demo-products/products";

  @GetMapping(value = PATH_PRODUCT)
  public List<ProductBean> getProducts();

  @GetMapping(value = PATH_PRODUCT + "/{reference}")
  public ProductBean getProductbyReference(@PathVariable("reference") String reference);
}
