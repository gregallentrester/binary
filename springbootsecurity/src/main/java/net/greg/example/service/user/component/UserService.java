package net.greg.example.service.user.component;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.greg.example.domain.User;
import net.greg.example.domain.UserCreateForm;
import net.greg.example.repository.UserRepository;


@Service
public class UserService implements net.greg.example.service.user.UserService {


static {
  System.err.println("\n\nOk static UserService");
}

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository value) {

System.err.println("\n\nOk @Autowired " + getClass() + ".UserService(UserRepository)");

    userRepository = value;
  }

  @Override
  public Optional<User> getUserById(long id) {

System.err.println("\n\nOk " + getClass() + ".getUserById " + id);

    return Optional.ofNullable(userRepository.findOne(id));
  }

  @Override
  public Optional<User> getUserByEmail(String email) {

System.err.println(
  "\n\nOk " + getClass() + ".getUserByEmail(String) " +
  email.replaceFirst("@.*", "@***"));

    return userRepository.findOneByEmail(email);
  }

  @Override
  public Collection<User> getAllUsers() {

System.err.println("\n\nOk " + getClass() + ".getAllUsers()");

    return userRepository.findAll(new Sort("email"));
  }

  @Override
  public User create(UserCreateForm form) {

System.err.println("\n\nOk " + getClass() + ".create(UserCreateForm) " + form);

    User user = new User();

    user.setEmail(form.getEmail());
    user.setRole(form.getRole());

    user.setPasswordHash(
      new BCryptPasswordEncoder().encode(
      form.getPassword()));

    return userRepository.save(user);
  }
}
