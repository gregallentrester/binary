package net.greg.example.domain;

import org.springframework.security.core.authority.AuthorityUtils;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;
    public User getUser() { return user; }


    public CurrentUser(User value) {

      super(
        value.getEmail(),
        value.getPasswordHash(),
        AuthorityUtils.createAuthorityList(
          value.getRole().toString()));
System.err.println("\n" + getClass() + ".CurrentUser(User)");
      user = value;
    }

    public Long getId() { return user.getId(); }
    public Role getRole() { return user.getRole(); }


    @Override
    public String toString() {

      return
        "CurrentUser{" +
        "user=" + user +
        "} " + super.toString();
  }
}
