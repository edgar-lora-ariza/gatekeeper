package com.bedrock.gatekeeper.users.usecases;

import com.bedrock.gatekeeper.users.model.UserRoles;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public abstract class UserUseCase {

  protected boolean isSelf(User user, User currentUser) {
    return user.getUsername().equals(currentUser.getUsername());
  }

  protected boolean isAdmin(User user) {
    return user.getAuthorities().contains(new SimpleGrantedAuthority(UserRoles.ROLE_ADMIN_CONSOLE_USER));
  }

  protected boolean isSuperAdmin(User user) {
    return user.getAuthorities().contains(new SimpleGrantedAuthority(UserRoles.ROLE_SUPER_ADMIN_CONSOLE_USER));
  }
}
