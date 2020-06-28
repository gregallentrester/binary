package net.greg.example.service.currentuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.greg.example.domain.CurrentUser;
import net.greg.example.domain.User;
import net.greg.example.service.user.UserService;


@Service
public class CurrentUserDetailsService implements UserDetailsService {

  private final UserService userService;

  @Autowired
  public CurrentUserDetailsService(UserService service) {

System.err.println("\n\nOk " + getClass() + ".CurrentUserDetailsService(UserService)");

    userService = service;
  }

  @Override
  public CurrentUser loadUserByUsername(String email)
    throws UsernameNotFoundException {

System.err.println(
  "\n\n" + getClass() + ".loadUserByUsername(String)" +
  "Authenticating user with email " +
  email.replaceFirst("@.*", "@***"));

    User user =
      userService.getUserByEmail(email).
        orElseThrow(() -> new UsernameNotFoundException(
         "User with email=" + email + " not found"));

    return new CurrentUser(user);
  }
}
