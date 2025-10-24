package com.bedrock.gatekeeper.users.usecases;

import com.bedrock.gatekeeper.commons.exceptions.BusinessException;
import com.bedrock.gatekeeper.commons.model.ErrorCodes;
import com.bedrock.gatekeeper.users.dtos.ChangePasswordDto;
import com.bedrock.gatekeeper.users.dtos.CreatePasswordDto;
import com.bedrock.gatekeeper.users.dtos.SetNewPasswordDto;
import com.bedrock.gatekeeper.users.ports.UserDataProvider;
import io.micrometer.observation.annotation.Observed;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Observed
public class ChangePasswordUseCase extends UserUseCase {

  private final UserDataProvider userDataProvider;
  private final PasswordEncoder passwordEncoder;

  public ChangePasswordUseCase(UserDataProvider userDataProvider) {
    this.userDataProvider = userDataProvider;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  public void createPassword(CreatePasswordDto request) {
    User userInSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (!request.password().equals(request.passwordConfirmation())) {
      throw new BusinessException("Error creating the password", ErrorCodes.INVALID_PASSWORD);
    }

    if (userDataProvider.userExists(userInSession.getUsername())) {
      throw new BusinessException("Error creating the password", ErrorCodes.USER_ALREADY_EXISTS);
    }

    User userToUpdate = new User(userInSession.getUsername(),
        "{bcrypt}" + passwordEncoder.encode(request.password()),
        userInSession.isEnabled(),
        userInSession.isAccountNonExpired(),
        userInSession.isCredentialsNonExpired(),
        userInSession.isAccountNonLocked(),
        userInSession.getAuthorities());

    userDataProvider.save(userToUpdate);
  }

  public void changePassword(ChangePasswordDto request) {
    User userInSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (!passwordEncoder.matches(request.oldPassword(), userInSession.getPassword())) {
      throw new BusinessException("Error changing the password", ErrorCodes.INVALID_PASSWORD);
    }

    User userToUpdate = new User(userInSession.getUsername(),
        passwordEncoder.encode(request.newPassword()),
        userInSession.isEnabled(),
        userInSession.isAccountNonExpired(),
        userInSession.isCredentialsNonExpired(),
        userInSession.isAccountNonLocked(),
        userInSession.getAuthorities());

    userDataProvider.save(userToUpdate);
  }

  public void setPasswordToUser(SetNewPasswordDto request) {
    User userInSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!isSuperAdmin(userInSession) && !isAdmin(userInSession)) {
      throw new BusinessException("Error setting the password", ErrorCodes.UNAUTHORIZED);
    }

    User targetUser = userDataProvider.findByUsername(request.username())
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.username()));

    if (isAdmin(userInSession) && isAdmin(targetUser)) {
      throw new BusinessException("Error setting the password", ErrorCodes.UNAUTHORIZED);
    }

    User userToUpdate = new User(userInSession.getUsername(),
        passwordEncoder.encode(request.newPassword()),
        userInSession.isEnabled(),
        userInSession.isAccountNonExpired(),
        userInSession.isCredentialsNonExpired(),
        userInSession.isAccountNonLocked(),
        userInSession.getAuthorities());

    userDataProvider.save(userToUpdate);
  }
}
