package com.bedrock.users.usecases;

import com.bedrock.users.model.CustomUser;
import com.bedrock.users.model.UserRoles;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public abstract class UserUseCase {

  protected boolean isSelf(CustomUser user, CustomUser currentUser) {
    return user.getUsername().equals(currentUser.getUsername());
  }

  protected boolean isAdmin(CustomUser user) {
    return user.getAuthorities().contains(new SimpleGrantedAuthority(UserRoles.ROLE_ADMIN_CONSOLE_USER));
  }

  protected boolean isSuperAdmin(CustomUser user) {
    return user.getAuthorities().contains(new SimpleGrantedAuthority(UserRoles.ROLE_SUPER_ADMIN_CONSOLE_USER));
  }
}
