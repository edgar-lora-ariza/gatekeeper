package com.white.label.gatekeeper.infrastructure.data.providers;

import com.white.label.gatekeeper.core.model.SigningKey;
import com.white.label.gatekeeper.infrastructure.data.providers.entities.SigningKeyEntity;
import com.white.label.gatekeeper.core.ports.SigningKeysPort;
import com.white.label.gatekeeper.infrastructure.data.providers.repositories.SigningKeysJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SigningKeysJpaDataProvider implements SigningKeysPort {

  private final SigningKeysJpaRepository signingKeysJpaRepository;

  public SigningKeysJpaDataProvider(SigningKeysJpaRepository signingKeysJpaRepository) {
    this.signingKeysJpaRepository = signingKeysJpaRepository;
  }

  @Override
  @Transactional
  public SigningKey addKey(String keyIdentifier, String certificate, String privateKey) {
    String id = UUID.randomUUID().toString();

    SigningKeyEntity signingKeyEntity = new SigningKeyEntity();
    signingKeyEntity.setId(id);
    signingKeyEntity.setKeyIdentifier(keyIdentifier);
    signingKeyEntity.setCertificate(certificate);
    signingKeyEntity.setPrivateKey(privateKey);

    SigningKeyEntity savedKey = signingKeysJpaRepository.save(signingKeyEntity);
    this.deactivateOtherKeys(savedKey.getId());

    return new SigningKey(signingKeyEntity.getId(),
        signingKeyEntity.getKeyIdentifier(),
        signingKeyEntity.getCertificate(),
        signingKeyEntity.getPrivateKey());
  }

  @Override
  public Optional<String> inactivateKey(String id) {
    Optional<SigningKeyEntity> signingKeyOptional = signingKeysJpaRepository.findById(id);

    if (signingKeyOptional.isPresent()) {
      SigningKeyEntity signingKeyEntity = signingKeyOptional.get();
      signingKeyEntity.setIsActive(false);
      return Optional.of(signingKeysJpaRepository.save(signingKeyEntity).getId());
    }

    return Optional.empty();
  }

  @Override
  public Optional<SigningKey> getActiveKey() {
    Optional<SigningKeyEntity> activeSigningKeyOptional = signingKeysJpaRepository
        .findByIsActive(true)
        .stream()
        .findFirst();

    if (activeSigningKeyOptional.isPresent()) {
      SigningKeyEntity signingKeyEntity = activeSigningKeyOptional.get();
      SigningKey signingKey = new SigningKey(signingKeyEntity.getId(),
          signingKeyEntity.getKeyIdentifier(),
          signingKeyEntity.getCertificate(),
          signingKeyEntity.getPrivateKey());
      return Optional.of(signingKey);
    }

    return Optional.empty();
  }

  @Override
  public Optional<SigningKey> getKeyById(String id) {
    Optional<SigningKeyEntity> signingKeyEntityOptional = signingKeysJpaRepository.findById(id);
    if (signingKeyEntityOptional.isPresent()) {
      SigningKeyEntity signingKeyEntity = signingKeyEntityOptional.get();

      return Optional.of(new SigningKey(signingKeyEntity.getId(),
          signingKeyEntity.getKeyIdentifier(),
          signingKeyEntity.getCertificate(),
          signingKeyEntity.getPrivateKey()));
    }

    return Optional.empty();
  }

  @Override
  public Optional<SigningKey> getKeyByKeyIdentifier(String keyIdentifier) {
    Optional<SigningKeyEntity> signingKeyEntityOptional = signingKeysJpaRepository.findByKeyIdentifier(keyIdentifier).stream()
        .findFirst();
    if (signingKeyEntityOptional.isPresent()) {
      SigningKeyEntity signingKeyEntity = signingKeyEntityOptional.get();

      return Optional.of(new SigningKey(signingKeyEntity.getId(),
          signingKeyEntity.getKeyIdentifier(),
          signingKeyEntity.getCertificate(),
          signingKeyEntity.getPrivateKey()));
    }

    return Optional.empty();
  }

  @Override
  public List<SigningKey> getAllKeys() {
    return signingKeysJpaRepository.findAll().stream()
        .map(signingKeyEntity -> new SigningKey(signingKeyEntity.getId(),
            signingKeyEntity.getKeyIdentifier(),
            signingKeyEntity.getCertificate(),
            signingKeyEntity.getPrivateKey()))
        .toList();
  }

  @Override
  public void saveAll(List<SigningKey> signingKeys) {
    List<SigningKeyEntity> signingKeyEntities = signingKeys.stream()
        .map(signingKey -> {
          SigningKeyEntity signingKeyEntity = new SigningKeyEntity();
          signingKeyEntity.setId(signingKey.id());
          signingKeyEntity.setKeyIdentifier(signingKey.keyIdentifier());
          signingKeyEntity.setCertificate(signingKey.certificate());
          signingKeyEntity.setPrivateKey(signingKey.privateKey());
          return  signingKeyEntity;
        }).toList();
    signingKeysJpaRepository.saveAll(signingKeyEntities);
  }

  private void deactivateOtherKeys(String id) {
    signingKeysJpaRepository.deactivateOtherKeys(id);
  }
}
