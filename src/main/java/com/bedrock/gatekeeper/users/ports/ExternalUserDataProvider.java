package com.bedrock.gatekeeper.users.ports;

import com.bedrock.gatekeeper.users.model.ExternalUser;
import java.util.Optional;

public interface ExternalUserDataProvider {
  void save(ExternalUser user);
  Optional<String> getIdByEmail(String email);
}
