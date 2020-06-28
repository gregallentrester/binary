package net.greg.examples.swagger;

import io.swagger.annotations.*;

import org.springframework.web.bind.annotation.*;


@RestController
@Api("Swagger API Documentation")
public class Controller {

  @GetMapping("/hello-swagger")
  @ApiOperation("Returns - String: 'hello from swagger'")
  public String get() {
    return "<h4>hello from swagger</h4>";
  }

  @PostMapping("/post-hello")
  @ApiOperation("This is the POST request")
  public String post() {
    return "This is the POST request";
  }
}
