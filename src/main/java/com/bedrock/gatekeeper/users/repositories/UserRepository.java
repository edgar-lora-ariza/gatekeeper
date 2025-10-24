package com.white.label.gatekeeper.infrastructure.data.providers.repositories;

import com.white.label.gatekeeper.infrastructure.data.providers.entities.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
  Optional<UserEntity> findByUsername(String username);
}
