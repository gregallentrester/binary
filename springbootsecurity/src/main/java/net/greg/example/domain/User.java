package net.greg.example.domain;

import javax.persistence.*;


/**
 * Perhaps connect to an external database engine, as described
 * in this article on BoneCP database connection pooling:
 *
 *   https://bit.ly/3bwaRuk
 */
@Entity
@Table(name = "user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;
  public Long getId() { return id; }

  @Column(name = "email", nullable = false, unique = true)
  private String email;
  public String getEmail() { return email; }
  public void setEmail(String value) { email = value; }

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String value) { passwordHash = value; }

  @Column(name = "role", nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role;
  public Role getRole() { return role; }
  public void setRole(Role value) { role = value; }


  @Override
  public String toString() {
    return
      "User{" +
      "id=" + id +
      ", email='" + email.replaceFirst("@.*", "@***") +
      ", passwordHash='" + passwordHash.substring(0, 10) +
      ", role=" + role +
      '}';
  }
}
