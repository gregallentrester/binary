package net.greg.examples.swagger;

import java.util.*;

import com.google.common.base.Predicate;

import org.springframework.context.annotation.*;

import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.*;

import springfox.documentation.spi.*;
import springfox.documentation.spring.web.plugins.*;
import springfox.documentation.swagger2.annotations.*;


// https://www.appsdeveloperblog.com/rest-api-contact-and-apiinfo-with-swagger/
@Configuration
@EnableSwagger2
public class Config {

  private final Predicate<RequestHandler> handler =
    RequestHandlerSelectors.basePackage(
      getClass().getPackage().getName());


  @Bean
  public Docket apiDocket() {

    Contact contact =
      new Contact(
       "App Template",
       "https://bit.ly/2YlIZFL",
       "gregallentrester@gmail.com");


    ApiInfo apiInfo =
        new ApiInfo(
          "RESTful Web Service", // banner
          "Documents Endpoints", // subtitle
          "1.0",
          "http://localhost:8080/tq.html",
          contact,
          "Apache 2.0",
          "http://www.apache.org/licenses/LICENSE-2.0",
          new ArrayList<VendorExtension>());

    return
      new Docket(DocumentationType.SWAGGER_2).
        apiInfo(apiInfo).
        select().
        apis(handler).
        paths(PathSelectors.any()).
        build();
  }
}
