package net.greg.example.service.currentuser.component;

import org.springframework.stereotype.Service;

import net.greg.example.domain.CurrentUser;
import net.greg.example.domain.Role;


@Service
public class CurrentUserService implements
  net.greg.example.service.currentuser.CurrentUserService {

  @Override
  public boolean canAccessUser(CurrentUser currentUser, Long userId) {

System.err.println(
  "\n\n" + getClass() + ".canAccessUser(CurrentUser Long)" +
  currentUser + " have access to user " + userId);

    return
      null != currentUser
      &&
      (currentUser.getRole() == Role.ADMIN ||
       currentUser.getId().equals(userId));
  }
}
