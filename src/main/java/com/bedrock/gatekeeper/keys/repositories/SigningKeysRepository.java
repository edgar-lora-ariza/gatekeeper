package com.bedrock.gatekeeper.keys.repositories;

import com.bedrock.gatekeeper.keys.entities.SigningKeyEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SigningKeysRepository extends JpaRepository<SigningKeyEntity, String> {

  List<SigningKeyEntity> findByIsActive(Boolean isActive);

  List<SigningKeyEntity> findByKeyIdentifier(String keyIdentifier);
}
