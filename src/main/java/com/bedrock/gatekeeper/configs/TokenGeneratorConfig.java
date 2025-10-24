package com.bedrock.gatekeeper.configs;

import com.bedrock.gatekeeper.keys.model.SigningKey;
import com.bedrock.gatekeeper.users.model.CustomUser;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.util.List;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

@Configuration
public class TokenGeneratorConfig {

  private static final String EMAIL = "email";
  private static final String SUB = "sub";
  public static final String AUTHORITY = "authority";

  @Bean
  public OAuth2TokenGenerator<OAuth2Token> tokenGenerator(JWKSource<SecurityContext> jwkSource, SigningKey activeKey) {
    NimbusJwtEncoder nimbusJwtEncoder = new NimbusJwtEncoder(jwkSource);
    JwtGenerator jwtGenerator = new JwtGenerator(nimbusJwtEncoder);
    jwtGenerator.setJwtCustomizer(context -> {
      this.setSignatureAlgorithm(jwkSource, activeKey, context);
      if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
        this.customizeAccessToken(context);
      }
    });

    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
    return new DelegatingOAuth2TokenGenerator(
        jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
  }

  private void setSignatureAlgorithm(JWKSource<SecurityContext> jwkSource, SigningKey activeKey, JwtEncodingContext context) {
    JWKSet jwkSet;
    try {
      JWKMatcher matcher = new JWKMatcher.Builder().keyID(activeKey.id()).build();
      JWKSelector selector = new JWKSelector(matcher);
      jwkSet = new JWKSet(jwkSource.get(selector, null));
    } catch (Exception e) {
      throw new IllegalStateException("Failed to get key with ID " + activeKey.id() + " from JWKSource", e);
    }

    List<JWK> keys = jwkSet.getKeys();
    if (keys.isEmpty()) {
      throw new IllegalStateException("Active signing key with ID " + activeKey.id() + " not found in JWKSource.");
    }

    JWK key = keys.getFirst();
    if (key instanceof RSAKey) {
      context.getJwsHeader().algorithm(SignatureAlgorithm.RS256);
    } else if (key instanceof ECKey) {
      context.getJwsHeader().algorithm(SignatureAlgorithm.ES256);
    }
  }

  private void customizeAccessToken(JwtEncodingContext context) {
    if (context.getPrincipal() instanceof OAuth2AuthenticationToken token) {
      DefaultOAuth2User user = (DefaultOAuth2User) token.getPrincipal();
      context.getClaims().claim(SUB, user.getName());
      context.getClaims().claim(EMAIL, Objects.requireNonNull(user.getAttribute(EMAIL)));
      context.getClaims().claim(AUTHORITY, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    } else if(context.getPrincipal() instanceof UsernamePasswordAuthenticationToken token)  {
      CustomUser user = (CustomUser) token.getPrincipal();
      context.getClaims().claim(SUB, user.getId());
      context.getClaims().claim(EMAIL, user.getUsername());
      context.getClaims().claim(AUTHORITY, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    } else {
      throw new UnsupportedOperationException("Unsupported token type: " + context.getTokenType());
    }
  }
}