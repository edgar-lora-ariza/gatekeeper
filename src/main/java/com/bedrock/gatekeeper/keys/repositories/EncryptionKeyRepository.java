package com.white.label.gatekeeper.infrastructure.data.providers.repositories;

import com.white.label.gatekeeper.infrastructure.data.providers.entities.EncryptionKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncryptionKeyJpaRepository extends JpaRepository<EncryptionKeyEntity, String> {
}
