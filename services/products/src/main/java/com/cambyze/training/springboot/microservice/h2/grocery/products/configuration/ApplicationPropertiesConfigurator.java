package com.cambyze.training.springboot.microservice.h2.grocery.products.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 
 * Declaration of configuration properties
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@Component
@ConfigurationProperties(prefix = "cambyze.training.grocery")
public class ApplicationPropertiesConfigurator {

  private int numberDecimals;

  public int getNumberDecimals() {
    return numberDecimals;
  }

  public void setNumberDecimals(int numberDecimals) {
    this.numberDecimals = numberDecimals;
  }



}
