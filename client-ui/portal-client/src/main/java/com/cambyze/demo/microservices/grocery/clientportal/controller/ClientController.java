package com.cambyze.demo.microservices.grocery.clientportal.controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cambyze.demo.microservices.grocery.clientportal.beans.ProductBean;
import com.cambyze.demo.microservices.grocery.clientportal.proxies.MicroserviceProductsProxy;

/**
 * Client controller to retrieve the products and format the text to be displayed
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
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
