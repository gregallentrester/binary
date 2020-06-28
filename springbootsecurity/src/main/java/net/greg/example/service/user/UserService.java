package net.greg.example.service.user;

import java.util.Collection;
import java.util.Optional;

import net.greg.example.domain.User;
import net.greg.example.domain.UserCreateForm;


public interface UserService {

default void any() {
  System.err.println("\n\nOk static UserService.any()");
}
  Optional<User> getUserById(long id);

  Optional<User> getUserByEmail(String email);

  Collection<User> getAllUsers();

  User create(UserCreateForm form);
}
