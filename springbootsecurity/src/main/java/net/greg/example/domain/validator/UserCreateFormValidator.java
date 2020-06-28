package net.greg.example.domain.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import net.greg.example.domain.UserCreateForm;
import net.greg.example.service.user.UserService;


@Component
public class UserCreateFormValidator implements Validator {

  private final UserService userService;

  @Autowired
  public UserCreateFormValidator(UserService value) {

System.err.println("\n" + getClass() + ".UserCreateFormValidator(UserService)");
    userService = value;
  }

  @Override
  public boolean supports(Class<?> value) {

System.err.println("\n" + getClass() + ".supports(Class<?>)");
    return value.equals(UserCreateForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {

System.err.println("\n\nOk " + getClass() + ".validate() " + target + errors);

    UserCreateForm form = (UserCreateForm) target;

    validatePasswords(errors, form);
    validateEmail(errors, form);
  }

  private void validatePasswords(Errors errors, UserCreateForm form) {

System.err.println("\n\nOk " + getClass() + ".validatePasswords(Errors, UserCreateForm)");

    if ( ! form.getPassword().equals(form.getPasswordRepeated())) {
      errors.reject("password.no_match", "Passwords do not match");
    }
  }

  private void validateEmail(Errors errors, UserCreateForm form) {

System.err.println("\n\nOk " + getClass() + ".validateEmail(Errors, UserCreateForm)");

    if (userService.getUserByEmail(form.getEmail()).isPresent()) {
      errors.reject("email.exists", "User with this email already exists");
    }
  }
}
