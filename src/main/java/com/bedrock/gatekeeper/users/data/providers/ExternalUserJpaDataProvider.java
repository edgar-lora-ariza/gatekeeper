package com.bedrock.gatekeeper.users.data.providers;

import com.bedrock.gatekeeper.users.entities.ExternalUserEntity;
import com.bedrock.gatekeeper.users.model.ExternalUser;
import com.bedrock.gatekeeper.users.ports.ExternalUserDataProvider;
import com.bedrock.gatekeeper.users.repositories.ExternalUserRepository;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
@Observed
@RequiredArgsConstructor
public class ExternalUserJpaDataProvider implements ExternalUserDataProvider {

  private final ExternalUserRepository externalUserRepository;
  private final ConversionService conversionService;

  @Override
  public void save(ExternalUser user) {
    externalUserRepository.save(Objects.requireNonNull(conversionService.convert(user, ExternalUserEntity.class)));
  }

  @Override
  public Optional<String> getIdByEmail(String email) {
    List<ExternalUserEntity> externalUserEntityByEmail = externalUserRepository.findExternalUserEntityByEmail(email);
    if (externalUserEntityByEmail.isEmpty()) {
      return Optional.empty();
    }

    return externalUserEntityByEmail.stream()
        .findFirst()
        .map(ExternalUserEntity::getId);
  }
}
