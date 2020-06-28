package net.greg.example.service.currentuser;

import net.greg.example.domain.CurrentUser;

public interface CurrentUserService {

  boolean canAccessUser(CurrentUser currentUser, Long userId);
}
