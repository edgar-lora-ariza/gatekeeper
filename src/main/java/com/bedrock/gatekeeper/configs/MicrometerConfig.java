package com.bedrock.gatekeeper.configs;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.http.server.observation.ServerRequestObservationConvention;

@Configuration
public class MicrometerConfig {

  private static final List<String> OAUTH2_ROUTES = List.of(
      "/oauth2/authorize",
      "/oauth2/token",
      "/oauth2/introspect",
      "/oauth2/revoke",
      "/oauth2/jwks",
      "/oauth2/device_authorization",
      "/oauth2/device_verification",
      "/.well-known/openid-configuration",
      "/.well-known/oauth-authorization-server",
      "/userinfo",
      "/connect/register"
  );

  @Bean
  public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
    return new ObservedAspect(observationRegistry);
  }

  @Bean
  public ServerRequestObservationConvention customServerRequestObservationConvention() {
    return new DefaultServerRequestObservationConvention() {

      @NotNull
      @Override
      public KeyValues getLowCardinalityKeyValues(@NotNull ServerRequestObservationContext context) {
        KeyValues keyValues = super.getLowCardinalityKeyValues(context);

        HttpServletRequest request = context.getCarrier();
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        String pathWithoutContext = requestUri.substring(contextPath.length());

        String httpRoute = OAUTH2_ROUTES.stream()
            .filter(pathWithoutContext::equals)
            .map(route -> contextPath + route)
            .findFirst()
            .orElse(null);

        if (httpRoute != null) {
          List<KeyValue> filteredList = new java.util.ArrayList<>();
          for (KeyValue kv : keyValues) {
            if (!"uri".equalsIgnoreCase(kv.getKey())) {
              filteredList.add(kv);
            }
          }
          filteredList.add(KeyValue.of("uri", httpRoute));
          return KeyValues.of(filteredList.toArray(new KeyValue[0]));
        }

        return keyValues;
      }
    };
  }
}
