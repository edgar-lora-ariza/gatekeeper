package com.bedrock.gatekeeper.users.usecases;

import com.bedrock.gatekeeper.users.ports.UserDataProvider;
import io.micrometer.observation.annotation.Observed;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Observed
public class DeleteUserUseCase {

  private final UserDataProvider userDataProvider;

  public DeleteUserUseCase(UserDataProvider userDataProvider) {
    this.userDataProvider = userDataProvider;
  }

  public void deleteUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    userDataProvider.deleteByUsername(username);
  }
}
