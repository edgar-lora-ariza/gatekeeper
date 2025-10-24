package com.bedrock.gatekeeper.users.delegates;

import com.bedrock.gatekeeper.users.model.ExternalUser;
import com.bedrock.gatekeeper.users.ports.ExternalUserDataProvider;
import com.bedrock.gatekeeper.users.ports.UserDataProvider;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class CustomOidcUserDelegate extends OidcUserService {

  private final UserDataProvider userDataProvider;
  private final ExternalUserDataProvider externalUserDataProvider;

  public CustomOidcUserDelegate(@Lazy UserDataProvider userDataProvider,
                                @Lazy ExternalUserDataProvider externalUserDataProvider) {
    this.userDataProvider = userDataProvider;
    this.externalUserDataProvider = externalUserDataProvider;
  }

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = super.loadUser(userRequest);

    Set<GrantedAuthority> authorities = new HashSet<>();
    userDataProvider.findByUsername(oidcUser.getEmail())
        .ifPresent(customUser -> authorities.addAll(customUser.getAuthorities()));

    String internalSub = externalUserDataProvider.getIdByEmail(oidcUser.getEmail())
        .orElseGet(() -> {
          String sub = UUID.randomUUID().toString();
          ExternalUser externalUser = ExternalUser.builder()
              .id(sub)
              .email(oidcUser.getEmail())
              .build();
          externalUserDataProvider.save(externalUser);
          return sub;
        });

    OidcIdToken originalIdToken = oidcUser.getIdToken();
    OidcIdToken newIdToken = null;
    if (Objects.nonNull(originalIdToken)) {
      Map<String, Object> modifiedIdTokenClaims = new HashMap<>(originalIdToken.getClaims());
      modifiedIdTokenClaims.put("sub", internalSub);
      newIdToken = new OidcIdToken(originalIdToken.getTokenValue(),
          originalIdToken.getIssuedAt(),
          originalIdToken.getExpiresAt(),
          modifiedIdTokenClaims);
    }

    OidcUserInfo originalUserInfo = oidcUser.getUserInfo();
    OidcUserInfo newUserInfo = null;
    if (Objects.nonNull(originalUserInfo)) {
      Map<String, Object> modifiedUserInfoClaims = new HashMap<>(originalUserInfo.getClaims());
      modifiedUserInfoClaims.put("sub", internalSub);
      newUserInfo = new OidcUserInfo(modifiedUserInfoClaims);
    }

    return new DefaultOidcUser(authorities, newIdToken, newUserInfo);
  }
}