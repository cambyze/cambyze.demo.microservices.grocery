package com.cambyze.demo.microservices.grocery.clientportal.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cambyze.demo.microservices.grocery.clientportal.exceptions.MicroservicesExceptionsController;
import feign.auth.BasicAuthRequestInterceptor;

/**
 * Feign configuration to
 * <ul>
 * <li>customise the Feign exception</li>
 * <li>force username/passwd in the header</li>
 * </ul>
 * 
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@Configuration
public class FeignConfig {

  @Bean
  public MicroservicesExceptionsController microservicesExceptionsController() {
    return new MicroservicesExceptionsController();
  }

  @Bean
  public BasicAuthRequestInterceptor changeBasicAuthRequest() {
    return new BasicAuthRequestInterceptor("cambyze-api-full-access",
        "e120b07e7734ed541d87d1846a23428b");
  }

}
