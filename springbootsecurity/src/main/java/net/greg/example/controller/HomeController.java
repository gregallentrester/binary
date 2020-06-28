package net.greg.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class HomeController {

  @RequestMapping("/")
  public String getHomePage() {

System.err.println(
  "\n\nOk " + getClass() + ".getHomePage()");

    return "home";
  }
}
