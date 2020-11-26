package com.cambyze.demo.microservices.grocery.gatewayserver.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * To allow to disable CSRF during development phase by setting the property
 * cambyze.security.csrf=false
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@Configuration
public class SpringBootSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${cambyze.security.csrf}")
  private boolean csrfEnabled = true;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    if (!csrfEnabled) {
      http.csrf().disable();
    }
  }
}
