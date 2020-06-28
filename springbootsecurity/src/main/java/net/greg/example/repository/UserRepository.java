package net.greg.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.greg.example.domain.User;


public interface UserRepository extends JpaRepository<User, Long> {
  java.util.Optional<User> findOneByEmail(String email);
}
