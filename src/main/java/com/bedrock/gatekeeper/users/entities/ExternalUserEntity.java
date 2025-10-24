package com.bedrock.gatekeeper.users.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "external_users", schema = "auth")
public class ExternalUserEntity {

  @Id
  private String id;

  @Column(nullable = false, unique = true)
  private String email;
}
