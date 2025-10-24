package com.bedrock.gatekeeper.keys.repositories;

import com.bedrock.gatekeeper.keys.entities.EncryptionKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncryptionKeyRepository extends JpaRepository<EncryptionKeyEntity, String> {
}
