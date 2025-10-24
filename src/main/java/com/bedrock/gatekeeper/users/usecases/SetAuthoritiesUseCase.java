package com.bedrock.gatekeeper.users.usecases;

import com.bedrock.gatekeeper.commons.exceptions.BusinessException;
import com.bedrock.gatekeeper.commons.model.ErrorCodes;
import com.bedrock.gatekeeper.users.dtos.SetAuthoritiesDto;
import com.bedrock.gatekeeper.users.model.UserRoles;
import com.bedrock.gatekeeper.users.ports.UserDataProvider;
import io.micrometer.observation.annotation.Observed;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Observed
public class SetAuthoritiesUseCase extends UserUseCase {

  private final UserDataProvider userDataProvider;

  public SetAuthoritiesUseCase(UserDataProvider userDataProvider) {
    this.userDataProvider = userDataProvider;
  }

  public User setAuthorities(SetAuthoritiesDto request) {
    User userInSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User targetUser = userDataProvider.findByUsername(request.username())
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.username()));

    Set<SimpleGrantedAuthority> authorities = request.authorities().stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());

    if (this.isSuperAdmin(userInSession)) {
      return superAdminSetAuthoritiesToUser(targetUser, userInSession, authorities);
    }

    return adminSetAuthoritiesToUser(targetUser, userInSession, authorities);
  }

  private boolean notHasAuthority(Set<SimpleGrantedAuthority> authorities, String authority) {
    return authorities.stream()
        .noneMatch(simpleGrantedAuthority -> simpleGrantedAuthority
            .getAuthority().equals(authority));
  }

  private User saveUser(User targetUser, Set<SimpleGrantedAuthority> authorities) {
    User updatedUser = new User(targetUser.getUsername(),
        targetUser.getPassword(),
        targetUser.isEnabled(),
        targetUser.isAccountNonExpired(),
        targetUser.isCredentialsNonExpired(),
        targetUser.isAccountNonLocked(),
        authorities);

    return userDataProvider.save(updatedUser);
  }

  private User superAdminSetAuthoritiesToUser(User targetUser, User currentUser,
                                              Set<SimpleGrantedAuthority> authorities) {
    if (this.isSelf(targetUser, currentUser)
        && this.notHasAuthority(authorities, UserRoles.ROLE_SUPER_ADMIN_CONSOLE_USER)) {
      throw new BusinessException("Error setting authorities",
          ErrorCodes.USER_CAN_NOT_REMOVE_REQUIRED_AUTHORITY);
    }

    return saveUser(targetUser, authorities);
  }

  private User adminSetAuthoritiesToUser(User targetUser, User currentUser, Set<SimpleGrantedAuthority> authorities) {
    if (this.isSelf(targetUser, currentUser)
        && this.notHasAuthority(authorities, UserRoles.ROLE_ADMIN_CONSOLE_USER)) {
      throw new BusinessException("Error setting authorities",
          ErrorCodes.USER_CAN_NOT_REMOVE_REQUIRED_AUTHORITY);
    }

    if (this.isAdmin(targetUser)
        && this.notHasAuthority(authorities, UserRoles.ROLE_ADMIN_CONSOLE_USER)) {
      throw new BusinessException("Error setting authorities",
          ErrorCodes.USER_CAN_NOT_UNAUTHORIZE_USER);
    }

    if (this.isSuperAdmin(targetUser)) {
      throw new BusinessException("Error setting authorities",
          ErrorCodes.USER_CAN_NOT_UNAUTHORIZE_SUPER_USER);
    }

    return saveUser(targetUser, authorities);
  }
}
