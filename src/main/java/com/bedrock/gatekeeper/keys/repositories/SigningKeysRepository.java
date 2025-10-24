package com.white.label.gatekeeper.infrastructure.data.providers.repositories;

import com.white.label.gatekeeper.infrastructure.data.providers.entities.SigningKeyEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SigningKeysJpaRepository extends JpaRepository<SigningKeyEntity, String> {

  @Modifying
  @Query(value = "UPDATE SigningKeyEntity sk SET sk.isActive = FALSE WHERE sk.id != :id")
  void deactivateOtherKeys(String id);

  List<SigningKeyEntity> findByIsActive(Boolean isActive);

  List<SigningKeyEntity> findByKeyIdentifier(String keyIdentifier);
}
