package net.greg.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.greg.example.service.user.UserService;


@Controller
public class UsersController {

  private final UserService userService;

  @Autowired
  public UsersController(UserService service) {

System.err.println("\n\nOk " + getClass() + ".UsersController(UserService)");
    userService = service;
  }

  @RequestMapping("/users")
  public ModelAndView getUsersPage() {

System.err.println("\n\nOk " + getClass() + ".getUsersPage()");

    return new ModelAndView(
      "users", "users", userService.getAllUsers());
  }
}
