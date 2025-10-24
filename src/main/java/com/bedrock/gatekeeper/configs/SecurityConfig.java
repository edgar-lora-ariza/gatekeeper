package com.bedrock.gatekeeper.configs;

import static com.bedrock.gatekeeper.keys.utils.PemUtils.enforcePemCertFormat;
import static com.bedrock.gatekeeper.keys.utils.PemUtils.enforcePemFormat;
import static org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.ES256;
import static org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.RS256;

import com.bedrock.gatekeeper.commons.views.LoginView;
import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import com.bedrock.gatekeeper.keys.model.SigningKey;
import com.bedrock.gatekeeper.keys.usecases.GetActiveSigningKeyUseCase;
import com.bedrock.gatekeeper.keys.usecases.GetAllSigningKeysUseCase;
import com.bedrock.gatekeeper.keys.usecases.GetEncryptionKeyUseCase;
import com.bedrock.gatekeeper.keys.usecases.SaveEncryptionKeyUseCase;
import com.bedrock.gatekeeper.keys.utils.PemUtils;
import com.bedrock.gatekeeper.users.delegates.CustomOidcUserDelegate;
import com.bedrock.gatekeeper.users.model.CustomUser;
import com.bedrock.gatekeeper.users.model.UserRoles;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponseType;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcProviderConfigurationEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfig {

  private static final String LOGIN = "/login";
  private static final String ID = "id";
  private static final String EMAIL = "email";

  private final boolean isSslEnable;
  private final String serverAddress;
  private final String serverPort;
  private final String contextPath;
  private final CustomOidcUserDelegate customOidcUserDelegate;

  public SecurityConfig(@Value("${server.ssl.enabled:false}") boolean isSslEnable,
                        @Value("${server.address:localhost}") String serverAddress,
                        @Value("${server.port}") String serverPort,
                        @Value("${server.servlet.context-path}") String contextPath,
                        CustomOidcUserDelegate customOidcUserDelegate) {
    this.isSslEnable = isSslEnable;
    this.serverAddress = serverAddress;
    this.serverPort = serverPort;
    this.contextPath = contextPath;
    this.customOidcUserDelegate = customOidcUserDelegate;
  }

  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
    Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = context -> {
      OAuth2Authorization authorization = context.getAuthorization();
      Authentication principalAuthentication = authorization.getAttribute(java.security.Principal.class.getName());

      Map<String, Object> claims = new HashMap<>();
      Object principal = Objects.requireNonNull(principalAuthentication).getPrincipal();

      if (principal instanceof DefaultOAuth2User defaultOAuth2User) {
        claims.put(ID, defaultOAuth2User.getAttribute("sub"));
        claims.put(EMAIL, defaultOAuth2User.getAttribute(EMAIL));
      } else if (principal instanceof CustomUser customUser) {
        claims.put(ID, customUser.getId());
        claims.put(EMAIL, customUser.getUsername());
      }

      return new OidcUserInfo(claims);
    };

    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer
        .authorizationServer();

    http
        .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
        .csrf(AbstractHttpConfigurer::disable)
        .with(authorizationServerConfigurer, authorizationServer -> authorizationServer
            .oidc(oidc -> oidc
                .userInfoEndpoint(configurer ->
                    configurer.userInfoMapper(userInfoMapper))
                .providerConfigurationEndpoint(this::setOidcProviderConfigurationEndpoint))
            .authorizationServerMetadataEndpoint(metadataEndpoint ->
                metadataEndpoint.authorizationServerMetadataCustomizer(metadataBuilder ->
                    metadataBuilder.dPoPSigningAlgorithms(algorithms -> {
                      algorithms.clear();
                      algorithms.add(ES256.getName());
                      algorithms.add(RS256.getName());
                    }))))
        .authorizeHttpRequests(authorize ->
            authorize.anyRequest().authenticated())
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(LOGIN)));

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/actuator/**")
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
            .anyRequest().access(AuthorityAuthorizationManager
                .hasAnyAuthority(UserRoles.ROLE_SUPER_ADMIN_CONSOLE_USER, UserRoles.ROLE_ADMIN_CONSOLE_USER)))
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

  @Bean
  @Order(3)
  public SecurityFilterChain openApiSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/v3/api-docs/**", "/swagger-ui/**")
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());

    return http.build();
  }

  @Bean
  @Order(4)
  public SecurityFilterChain loginAndVaadinSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/", LOGIN, "/VAADIN/**", "/oauth2/**")
        .oauth2Login(oauth2 -> oauth2
            .loginPage(LOGIN)
            .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserDelegate)))
        .with(VaadinSecurityConfigurer.vaadin(), vaadin -> {
          vaadin.loginView(LoginView.class);
          vaadin.oauth2LoginPage(LOGIN);
          vaadin.anyRequest(AuthorizeHttpRequestsConfigurer.AuthorizedUrl::permitAll);
        });

    return http.build();
  }

  @Bean
  @Order(5)
  public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/api/**")
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers(HttpMethod.POST, "/account").permitAll();
          auth.anyRequest().authenticated();
        })
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

  @Bean
  @Order(6)
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().permitAll());

    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthoritiesClaimName("authority");
    grantedAuthoritiesConverter.setAuthorityPrefix("");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder()
        .multipleIssuersAllowed(true)
        .build();
  }

  @Bean
  public EncryptionKey getEncryptionKey(GetEncryptionKeyUseCase getEncryptionKeyUseCase,
                                        SaveEncryptionKeyUseCase saveEncryptionKeyUseCase) {
    Optional<EncryptionKey> encryptionKey = getEncryptionKeyUseCase.getEncryptionKey();
    if (encryptionKey.isPresent()) {
      return encryptionKey.get();
    }

    String newEncryptionKey = UUID.randomUUID().toString();
    saveEncryptionKeyUseCase.saveEncryptionKey(newEncryptionKey);
    return new EncryptionKey(newEncryptionKey);
  }

  @Bean
  public SigningKey getActiveSigningKey(GetActiveSigningKeyUseCase getActiveSigningKeyUseCase) {
    return getActiveSigningKeyUseCase.getActiveSigningKey();
  }

  @Bean
  public X509Certificate getCertificate(SigningKey signingKey)
      throws IOException, CertificateException {
    String pemCertEnforcedContent = enforcePemCertFormat(signingKey.certificate());
    String pemEnforcedContent = enforcePemFormat(pemCertEnforcedContent);
    StringReader reader = new StringReader(pemEnforcedContent);
    PEMParser pemParser = new PEMParser(reader);
    Object parsedObject = pemParser.readObject();

    if (parsedObject instanceof X509CertificateHolder certificateHolder) {
      CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
      return (X509Certificate) certFactory
          .generateCertificate(new ByteArrayInputStream(certificateHolder.getEncoded()));
    } else {
      throw new CertificateException("Unexpected object found in PEM: " + parsedObject.getClass().getName());
    }
  }

  @Bean
  public PublicKey getPublicKey(X509Certificate certificate) {
    return certificate.getPublicKey();
  }

  @Bean
  public PrivateKey getPrivateKey(SigningKey signingKey) throws IOException {
    return PemUtils.getPrivateKey(signingKey.privateKey());
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource(GetAllSigningKeysUseCase getAllSigningKeysUseCase) {
    return ((_, _) -> {
      List<JWK> jwkList = getAllSigningKeysUseCase.getAllSigningKeys().stream()
          .map(
              signingKey -> {
                try {
                  PrivateKey privateKey = PemUtils.getPrivateKey(signingKey.privateKey());
                  Certificate certificate = PemUtils.getCertificate(signingKey.certificate());
                  PublicKey publicKey = PemUtils.getPublicKey(certificate);

                  switch (publicKey) {
                    case RSAPublicKey rsaPublicKey -> {
                      return new RSAKey.Builder(rsaPublicKey)
                          .privateKey(privateKey)
                          .keyID(signingKey.id())
                          .build();
                    }
                    case ECPublicKey ecPublicKey -> {
                      ECPrivateKey ecPrivateKey = (ECPrivateKey) privateKey;
                      Curve curve = Curve.forECParameterSpec(ecPublicKey.getParams());
                      return new ECKey.Builder(curve, ecPublicKey)
                          .privateKey(ecPrivateKey)
                          .keyID(signingKey.id())
                          .build();
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + publicKey);
                  }
                } catch (IOException | CertificateException e) {
                  log.error("Failed to load signing key: {}", e.getMessage(), e);
                  throw new IllegalStateException("Failed to load signing key", e);
                }
              })
          .map(JWK.class::cast)
          .toList();

      return new JWKSet(jwkList).getKeys();
    });
  }

  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

  private void setClientMethods(List<String> methods) {
    methods.clear();
    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue());
    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue());
    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_JWT.getValue());
    methods.add(ClientAuthenticationMethod.PRIVATE_KEY_JWT.getValue());
    methods.add(ClientAuthenticationMethod.TLS_CLIENT_AUTH.getValue());
    methods.add(ClientAuthenticationMethod.SELF_SIGNED_TLS_CLIENT_AUTH.getValue());
  }

  private void setOidcProviderConfigurationEndpoint(OidcProviderConfigurationEndpointConfigurer configurer) {
    configurer.providerConfigurationCustomizer(builder -> {
      String protocol = isSslEnable ? "https" : "http";
      String issuer = protocol + "://" + serverAddress + ":" + serverPort + contextPath;
      AuthorizationServerSettings authorizationServerSettings = AuthorizationServerSettings.builder()
          .issuer(issuer)
          .build();

      builder.issuer(issuer);
      builder.authorizationEndpoint(issuer + authorizationServerSettings.getAuthorizationEndpoint());
      builder.pushedAuthorizationRequestEndpoint(issuer + authorizationServerSettings
          .getPushedAuthorizationRequestEndpoint());
      builder.deviceAuthorizationEndpoint(issuer + authorizationServerSettings.getDeviceAuthorizationEndpoint());
      builder.tokenEndpoint(issuer + authorizationServerSettings.getTokenEndpoint());
      builder.tokenEndpointAuthenticationMethods(this::setClientMethods);
      builder.jwkSetUrl(issuer + authorizationServerSettings.getJwkSetEndpoint());
      builder.userInfoEndpoint(issuer + "/userinfo");
      builder.endSessionEndpoint(issuer + authorizationServerSettings.getAuthorizationEndpoint());
      builder.responseTypes(responseTypes -> {
        responseTypes.clear();
        responseTypes.add(OAuth2AuthorizationResponseType.CODE.getValue());
      });
      builder.grantTypes(grantTypes -> {
        grantTypes.clear();
        grantTypes.add(AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
        grantTypes.add(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());
        grantTypes.add(AuthorizationGrantType.REFRESH_TOKEN.getValue());
        grantTypes.add(AuthorizationGrantType.DEVICE_CODE.getValue());
        grantTypes.add(AuthorizationGrantType.TOKEN_EXCHANGE.getValue());
      });
      builder.tokenRevocationEndpoint(issuer + authorizationServerSettings.getTokenRevocationEndpoint());
      builder.tokenRevocationEndpointAuthenticationMethods(this::setClientMethods);
      builder.tokenIntrospectionEndpoint(issuer + authorizationServerSettings.getTokenIntrospectionEndpoint());
      builder.tokenIntrospectionEndpointAuthenticationMethods(this::setClientMethods);
      builder.codeChallengeMethods(codeChallenges -> {
        codeChallenges.clear();
        codeChallenges.add(CodeChallengeMethod.S256.getValue());
      });
      builder.tlsClientCertificateBoundAccessTokens(true);
      builder.dPoPSigningAlgorithms(dPoPSigningAlgorithms -> {
        dPoPSigningAlgorithms.clear();
        dPoPSigningAlgorithms.add(ES256.getName());
        dPoPSigningAlgorithms.add(RS256.getName());
      });
      builder.subjectTypes(subjectTypes -> {
        subjectTypes.clear();
        subjectTypes.add(SubjectType.PUBLIC.name());
      });
      builder.idTokenSigningAlgorithms(idTokenSigningAlgorithms -> {
        idTokenSigningAlgorithms.clear();
        idTokenSigningAlgorithms.add(ES256.getName());
      });
      builder.scopes(scopes -> {
        scopes.clear();
        scopes.add("openid");
        scopes.add("profile");
        scopes.add(EMAIL);
      });
    });
  }
}
