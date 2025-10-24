package com.bedrock.gatekeeper.users.delegates;

import com.bedrock.gatekeeper.users.ports.UserDataProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsDelegates implements UserDetailsService {

  private final UserDataProvider userDataProvider;


  public UserDetailsDelegates(UserDataProvider userDataProvider) {
    this.userDataProvider = userDataProvider;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userDataProvider.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}
