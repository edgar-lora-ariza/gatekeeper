package com.bedrock.gatekeeper.users.usecases;

import com.bedrock.gatekeeper.users.model.UserRoles;
import com.bedrock.gatekeeper.users.ports.UserDataProvider;
import io.micrometer.observation.annotation.Observed;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Observed
public class InitUserUserCase {

  private final UserDataProvider userDataProvider;
  private final PasswordEncoder passwordEncoder;
  private final String adminUsername;
  private final String adminPassword;

  public InitUserUserCase(UserDataProvider userDataProvider,
                          @Value("${gatekeeper.admin-console.init.admin.email}") String adminUsername,
                          @Value("${gatekeeper.admin-console.init.admin.password}") String adminPassword) {
    this.userDataProvider = userDataProvider;
    this.passwordEncoder = new BCryptPasswordEncoder();
    this.adminUsername = adminUsername;
    this.adminPassword = adminPassword;
  }

  public void init() {
    if (!userDataProvider.userExists(adminUsername)) {
      SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(UserRoles.ROLE_SUPER_ADMIN_CONSOLE_USER);

      User superAdminUser = new User(adminUsername,
          "{bcrypt}" + passwordEncoder.encode(adminPassword),
          true,
          true,
          true,
          true,
          Collections.singleton(grantedAuthority));

      userDataProvider.save(superAdminUser);
    }
  }
}
