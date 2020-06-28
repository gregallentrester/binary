package net.greg.example.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import net.greg.example.domain.CurrentUser;


@ControllerAdvice
public class CurrentUserControllerAdvice {

  @ModelAttribute("currentUser")
  public CurrentUser getCurrentUser(Authentication authentication) {

System.err.println(
  "\n\nOk " + getClass() +
  ".getCurrentUser(Authentication)");
  
    return (authentication == null) ? null : (CurrentUser) authentication.getPrincipal();
  }
}
