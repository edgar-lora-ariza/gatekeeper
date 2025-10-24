package com.bedrock.gatekeeper.keys.data.providers;

import com.bedrock.gatekeeper.keys.entities.EncryptionKeyEntity;
import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import com.bedrock.gatekeeper.keys.ports.EncryptionKeyDataProvider;
import com.bedrock.gatekeeper.keys.repositories.EncryptionKeyRepository;
import io.micrometer.observation.annotation.Observed;
import java.util.Objects;
import org.springframework.core.convert.ConversionService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Observed
public class EncryptionKeyJpaDataProvider implements EncryptionKeyDataProvider {

  private final EncryptionKeyRepository encryptionKeyRepository;
  private final ConversionService conversionService;

  public EncryptionKeyJpaDataProvider(EncryptionKeyRepository encryptionKeyRepository,
      ConversionService conversionService) {
    this.encryptionKeyRepository = encryptionKeyRepository;
    this.conversionService = conversionService;
  }

  @Override
  public Optional<EncryptionKey> getEncryptionKey() {
    return encryptionKeyRepository.findAll().stream()
        .findFirst()
        .map(entity -> conversionService.convert(entity, EncryptionKey.class));
  }

  @Override
  public void saveEncryptionKey(EncryptionKey encryptionKey) {
    EncryptionKeyEntity entity = conversionService.convert(encryptionKey, EncryptionKeyEntity.class);
    encryptionKeyRepository.save(Objects.requireNonNull(entity));
  }

  @Override
  public void deleteEncryptionKey() {
    encryptionKeyRepository.deleteAll();
  }
}
