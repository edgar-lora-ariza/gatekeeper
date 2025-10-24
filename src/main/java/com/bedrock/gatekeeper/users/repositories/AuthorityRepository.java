package com.white.label.gatekeeper.infrastructure.data.providers.repositories;

import com.white.label.gatekeeper.infrastructure.data.providers.entities.AuthorityEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {
  Optional<AuthorityEntity> findByName(String name);
}
