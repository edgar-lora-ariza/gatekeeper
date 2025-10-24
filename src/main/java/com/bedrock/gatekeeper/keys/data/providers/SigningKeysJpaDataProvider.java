package com.bedrock.gatekeeper.keys.data.providers;

import com.bedrock.gatekeeper.keys.entities.SigningKeyEntity;
import com.bedrock.gatekeeper.keys.model.SigningKey;
import com.bedrock.gatekeeper.keys.ports.SigningKeysDataProvider;
import com.bedrock.gatekeeper.keys.repositories.SigningKeysRepository;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Optional;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Observed
public class SigningKeysJpaDataProvider implements SigningKeysDataProvider {

  private final SigningKeysRepository signingKeysRepository;
  private final ConversionService conversionService;

  public SigningKeysJpaDataProvider(SigningKeysRepository signingKeysRepository,
                                    ConversionService conversionService) {
    this.signingKeysRepository = signingKeysRepository;
    this.conversionService = conversionService;
  }

  @Override
  @Transactional
  public SigningKey addKey(String keyIdentifier, String certificate, String privateKey) {

    SigningKeyEntity signingKeyEntity = new SigningKeyEntity();
    signingKeyEntity.setKeyIdentifier(keyIdentifier);
    signingKeyEntity.setCertificate(certificate);
    signingKeyEntity.setPrivateKey(privateKey);

    SigningKeyEntity savedKey = signingKeysRepository.save(signingKeyEntity);
    this.deactivateOtherKeys(savedKey.getId());

    return conversionService.convert(savedKey, SigningKey.class);
  }

  @Override
  public Optional<SigningKey> getActiveKey() {
    return signingKeysRepository.findByIsActive(true).stream()
        .findFirst()
        .map(entity -> conversionService.convert(entity, SigningKey.class));
  }

  @Override
  public Optional<SigningKey> getKeyByKeyIdentifier(String keyIdentifier) {
    return signingKeysRepository.findByKeyIdentifier(keyIdentifier).stream().findFirst()
        .map(entity -> conversionService.convert(entity, SigningKey.class));
  }

  @Override
  public List<SigningKey> getAllKeys() {
    return signingKeysRepository.findAll().stream().map(
        entity -> conversionService.convert(entity, SigningKey.class)
    ).toList();
  }

  @Override
  public void saveAll(List<SigningKey> signingKeys) {
    List<SigningKeyEntity> signingKeyEntities = signingKeys.stream()
        .map(key -> conversionService.convert(key, SigningKeyEntity.class)).toList();
    signingKeysRepository.saveAll(signingKeyEntities);
  }

  @Override
  public void deleteAll(List<SigningKey> signingKeys) {
    signingKeysRepository.deleteAllById(signingKeys.stream().map(SigningKey::id).toList());
  }

  private void deactivateOtherKeys(String id) {
    List<SigningKeyEntity> otherActiveKeys = signingKeysRepository.findByIsActive(true).stream()
        .filter(key -> !key.getId().equals(id))
        .toList();

    otherActiveKeys.forEach(key -> key.setIsActive(false));
    signingKeysRepository.saveAll(otherActiveKeys);
  }
}
