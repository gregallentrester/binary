package net.greg.example.controller;

import javax.validation.Valid;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import net.greg.example.domain.UserCreateForm;
import net.greg.example.domain.validator.UserCreateFormValidator;
import net.greg.example.service.user.UserService;


@Controller
public class UserController {

  private final UserService userService;
  private final UserCreateFormValidator userCreateFormValidator;

  @Autowired
  public UserController(UserService service, UserCreateFormValidator validator) {
    userService = service;
    userCreateFormValidator = validator;
  }

  @InitBinder("form")
  public void initBinder(WebDataBinder binder) {

System.err.println("\n" + getClass() + ".initBinder(WebDataBinder)" + binder);

    binder.addValidators(userCreateFormValidator);
  }

  @PreAuthorize("@currentUserService.canAccessUser(principal, #id)")
  @RequestMapping("/user/{id}")
  public ModelAndView getUserPage(@PathVariable Long id) {

System.err.println("\n" + getClass() + ".getUserPage()" + id);

    return new ModelAndView(
      "user", "user", userService.getUserById(id).
      orElseThrow(() -> new NoSuchElementException(
        "User not found " + id)));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @RequestMapping(value = "/user/create", method = RequestMethod.GET)
  public ModelAndView getUserCreatePage() {

    System.err.println("\n" + getClass() + ".getUsercreatePage()");
    return new ModelAndView(
     "user_create", "form ", new UserCreateForm());
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @RequestMapping(value = "/user/create", method = RequestMethod.POST)
  public String handleUserCreateForm(@Valid @ModelAttribute("form") UserCreateForm form, BindingResult bindingResult) {

System.err.println(
    "\n\nOk " + getClass() +
    ".handleUserCreateForm(Errors, UserCreateForm, BindingResult)" +
    "Processing user create form " + form +
    "bindingResult " + bindingResult);

    if (bindingResult.hasErrors()) {
      return "user_create";
    }

    try {

      userService.create(form);
    }
    catch (DataIntegrityViolationException e) {

      // probably email already exists - very rare case when multiple admins are adding same user
      // at the same time and form validation has passed for more than one of them.
      System.err.println(
        "User Save Exception - assuming duplicate email" + e);

      bindingResult.reject("email.exists", "Email already exists");

      return "user_create";
    }

    return "redirect:/users";
  }
}
