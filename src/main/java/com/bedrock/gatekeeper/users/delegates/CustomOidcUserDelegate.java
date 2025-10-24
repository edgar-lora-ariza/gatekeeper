package com.bedrock.gatekeeper.users.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

  /**
   * This service is invoked after a successful authentication with an OIDC provider (e.g., Google).
   * Its purpose is to load the user's details. By default, we simply delegate to the parent
   * implementation, which creates a standard OidcUser with basic authorities (like ROLE_USER).
   * We do NOT map this external user to a local user or grant any administrative roles.
   * This ensures that social logins are strictly separated from administrative accounts.
   */
  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    return super.loadUser(userRequest);
  }
}