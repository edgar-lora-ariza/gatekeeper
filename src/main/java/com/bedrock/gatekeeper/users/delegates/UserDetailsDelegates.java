package com.bedrock.gatekeeper.users.adapters;

import com.bedrock.gatekeeper.commons.exceptions.BusinessException;
import com.bedrock.gatekeeper.commons.model.ErrorCodes;
import com.bedrock.gatekeeper.users.ports.UserDataProvider;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsAdapter implements UserDetailsManager {

  private final ConversionService conversionService;
  private final UserDataProvider userDataProvider;
  private final PasswordEncoder passwordEncoder;


  public UserDetailsAdapter(ConversionService conversionService,
                            UserDataProvider userDataProvider,
                            PasswordEncoder passwordEncoder) {
    this.conversionService = conversionService;
    this.userDataProvider = userDataProvider;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userDataProvider.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Override
  public void createUser(UserDetails user) {
    userDataProvider.save(conversionService.convert(user, User.class));
  }

  @Override
  public void updateUser(UserDetails user) {
    userDataProvider.save(conversionService.convert(user, User.class));
  }

  @Override
  public void deleteUser(String username) {
    userDataProvider.deleteByUsername(username);
  }

  @Override
  public void changePassword(String oldPassword, String newPassword) {
    UserDetails userInSession = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (!passwordEncoder.matches(oldPassword, userInSession.getPassword())) {
      throw new BusinessException("Error changing password", ErrorCodes.INVALID_PASSWORD);
    }

    UserDetails userDetails = User.withUserDetails(userInSession)
        .password(passwordEncoder.encode(newPassword))
        .build();

    User user = conversionService.convert(userDetails, User.class);

    userDataProvider.save(user);
  }

  @Override
  public boolean userExists(String username) {
    return userDataProvider.userExists(username);
  }
}
