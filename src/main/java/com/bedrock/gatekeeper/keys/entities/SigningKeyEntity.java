package com.white.label.gatekeeper.infrastructure.data.providers.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "signing_keys", schema = "keys")
public class SigningKeyEntity {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "key_identifier", unique = true, nullable = false)
  private String keyIdentifier;

  @Column(name = "certificate", columnDefinition = "TEXT", nullable = false)
  private String certificate;

  @Column(name = "private_key", columnDefinition = "TEXT", nullable = false)
  private String privateKey;

  @Column(name = "is_active", columnDefinition = "boolean default true", nullable = false)
  private Boolean isActive = true;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "rotated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime rotatedAt;
}
