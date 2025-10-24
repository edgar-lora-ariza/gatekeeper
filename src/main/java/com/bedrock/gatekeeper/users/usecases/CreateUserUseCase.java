package com.bedrock.gatekeeper.users.usecases;

import com.bedrock.gatekeeper.commons.exceptions.BusinessException;
import com.bedrock.gatekeeper.commons.model.ErrorCodes;
import com.bedrock.gatekeeper.users.dtos.CreateAccountDto;
import com.bedrock.gatekeeper.users.model.UserRoles;
import com.bedrock.gatekeeper.users.ports.UserDataProvider;
import io.micrometer.observation.annotation.Observed;
import java.util.Collections;
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Observed
public class CreateUserUseCase {

  private final UserDataProvider userDataProvider;
  private final PasswordEncoder passwordEncoder;

  public CreateUserUseCase(UserDataProvider userDataProvider) {
    this.userDataProvider = userDataProvider;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  public void createUser(CreateAccountDto request) {
    if (userDataProvider.userExists(request.email())) {
      throw new BusinessException("Error creating the account", ErrorCodes.USER_ALREADY_EXISTS);
    }

    if (!request.password().equals(request.passwordConfirmation())) {
      throw new BusinessException("Error creating the password", ErrorCodes.INVALID_PASSWORD);
    }

    User user = new User(request.email(),
        "{bcrypt}" + passwordEncoder.encode(request.password()),
        Collections.emptyList());
    userDataProvider.save(user);
  }

  public void createAdminUser(CreateAccountDto request) {
    if (!request.password().equals(request.passwordConfirmation())) {
      throw new BusinessException("Error creating the password", ErrorCodes.INVALID_PASSWORD);
    }

    if (userDataProvider.userExists(request.email())) {
      throw new BusinessException("Error creating the account", ErrorCodes.USER_ALREADY_EXISTS);
    }

    User user = new User(request.email(),
        "{bcrypt}" + passwordEncoder.encode(request.password()),
        Set.of(new SimpleGrantedAuthority(UserRoles.ROLE_ADMIN_CONSOLE_USER)));
    userDataProvider.save(user);
  }
}
