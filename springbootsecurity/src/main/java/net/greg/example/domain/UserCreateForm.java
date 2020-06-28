package net.greg.example.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * DTO between the web and service layers.
 * Annotated by Hibernate Validator validation constraints
 * to set some sane defaults.
 *
 * Notice that it's slightly different than User object,
 * therefore as much as I'd wish to "leak" the User entity
 * into the web layer I really cannot.
 */
public class UserCreateForm {

  @NotEmpty
  private String email = "";
  public String getEmail() { return email; }
  public void setEmail(String value) { email = value; }

  @NotEmpty
  private String password = "";
  public String getPassword() { return password; }
  public void setPassword(String value) { password = value; }

  @NotEmpty
  private String passwordRepeated = "";
  public String getPasswordRepeated() { return passwordRepeated;  }
  public void setPasswordRepeated(String value) { passwordRepeated = value; }

  @NotNull
  private Role role = Role.USER;
  public Role getRole() { return role;  }
  public void setRole(Role value) { role = value; }


  @Override
  public String toString() {
    return
      "UserCreateForm{" +
      "email='" + email.replaceFirst("@.+", "@***") + '\'' +
      ", password=***" + '\'' +
      ", passwordRepeated=***" + '\'' +
      ", role=" + role +
    '}';
  }
}
