package com.cambyze.training.springboot.microservice.h2.grocery.payments.configuration;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * Swagger configuration class for API documentation generation
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

  private Contact contact = new Contact("", "", "");
  private List<VendorExtension> vendorExtensions = new ArrayList<VendorExtension>();
  private ApiInfo apiInfo = new ApiInfo("Grocery payments management API",
      "API to manage grocery payments", "1.0", "", contact, "", "", vendorExtensions);
  private Tag PaymentControllerTag = new Tag("PaymentController", "Payment resource");

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo).select()
        .apis(RequestHandlerSelectors
            .basePackage("com.cambyze.training.springboot.microservice.h2.grocery.payments.web"))
        .paths(PathSelectors.any()).build().tags(PaymentControllerTag);
  }
}
