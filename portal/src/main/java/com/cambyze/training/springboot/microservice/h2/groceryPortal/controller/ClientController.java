package com.cambyze.training.springboot.microservice.h2.groceryPortal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ClientController {

  @RequestMapping("/")
  public String home(Model model) {

    return "home";
  }

}
