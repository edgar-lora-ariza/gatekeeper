package com.bedrock.gatekeeper.commons.service;

import com.bedrock.gatekeeper.users.model.UserRoles;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unused")
public class SecurityService {

  public boolean superAdminAndAdminAllowed() {
    return rolesAllowed(UserRoles.ROLE_SUPER_ADMIN_CONSOLE_USER, UserRoles.ROLE_ADMIN_CONSOLE_USER);
  }

  public boolean superAdminAllowed() {
    return rolesAllowed(UserRoles.ROLE_SUPER_ADMIN_CONSOLE_USER);
  }

  private boolean rolesAllowed(String... roles) {
    if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Jwt jwt) {
      List<String> authority = jwt.getClaimAsStringList("authority");
      return authority.stream().anyMatch(role -> Arrays.asList(roles).contains(role));
    }

    return false;
  }
}
