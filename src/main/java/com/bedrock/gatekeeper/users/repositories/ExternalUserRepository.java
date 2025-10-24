package com.bedrock.gatekeeper.users.repositories;

import com.bedrock.gatekeeper.users.entities.ExternalUserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalUserRepository extends JpaRepository<ExternalUserEntity, String> {
  List<ExternalUserEntity> findExternalUserEntityByEmail(String email);
}
