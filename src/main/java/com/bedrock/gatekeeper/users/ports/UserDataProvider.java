package com.bedrock.gatekeeper.users.ports;

import com.bedrock.gatekeeper.users.model.CustomUser;
import java.util.Optional;
import org.springframework.security.core.userdetails.User;

public interface UserDataProvider {
  CustomUser save(User user);

  Optional<CustomUser> findByUsername(String username);

  void deleteByUsername(String username);

  boolean userExists(String username);
}
