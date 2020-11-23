package com.cambyze.training.springboot.microservice.h2.groceryPortal.controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cambyze.training.springboot.microservice.h2.groceryPortal.beans.ProductBean;
import com.cambyze.training.springboot.microservice.h2.groceryPortal.proxies.MicroserviceProductsProxy;

@Controller
public class ClientController {

  @Autowired
  MicroserviceProductsProxy microserviceProductsProxy;

  @RequestMapping("/")
  public String home(Model model) {

    List<ProductBean> products = microserviceProductsProxy.getProducts();
    model.addAttribute("products", products);
    Locale locale = new Locale("fr", "FR");
    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
    for (ProductBean product : products) {
      product.setTextPrice("Price: " + fmt.format(product.getPrice()));
      product.setTextStock("current stock: " + product.getAvailable());
    }

    return "home";
  }

}
