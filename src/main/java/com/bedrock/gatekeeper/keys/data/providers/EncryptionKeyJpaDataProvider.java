package com.white.label.gatekeeper.infrastructure.data.providers;

import com.white.label.gatekeeper.infrastructure.data.providers.entities.EncryptionKeyEntity;
import com.white.label.gatekeeper.infrastructure.data.providers.repositories.EncryptionKeyJpaRepository;
import com.white.label.gatekeeper.core.model.EncryptionKey;
import com.white.label.gatekeeper.core.ports.EncryptionKeyPort;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class EncryptionKeyJpaDataProvider implements EncryptionKeyPort {

  private final EncryptionKeyJpaRepository encryptionKeyJpaRepository;

  public EncryptionKeyJpaDataProvider(EncryptionKeyJpaRepository encryptionKeyJpaRepository) {
    this.encryptionKeyJpaRepository = encryptionKeyJpaRepository;
  }

  @Override
  public Optional<EncryptionKey> getEncryptionKey() {
    return encryptionKeyJpaRepository.findAll().stream()
        .map(EncryptionKeyEntity::getEncryptionKey)
        .map(EncryptionKey::new)
        .findFirst();
  }

  @Override
  public void saveEncryptionKey(String encryptionKey) {
    EncryptionKeyEntity encryptionKeyEntity = new EncryptionKeyEntity();
    encryptionKeyEntity.setEncryptionKey(encryptionKey);
    encryptionKeyJpaRepository.save(encryptionKeyEntity);
  }

  @Override
  public void deleteEncryptionKey() {
    encryptionKeyJpaRepository.deleteAll();
  }
}
