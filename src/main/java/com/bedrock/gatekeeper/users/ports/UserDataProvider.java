package com.white.label.gatekeeper.core.ports;

import java.util.Optional;
import org.springframework.security.core.userdetails.User;

public interface UserPort {
  User save(User user);

  Optional<User> findByUsername(String username);

  void deleteByUsername(String username);

  boolean userExists(String username);
}
