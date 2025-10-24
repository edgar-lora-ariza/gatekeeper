package com.white.label.gatekeeper.infrastructure.data.providers.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@Entity
@Table(name = "authorities", schema = "auth")
public class AuthorityEntity implements GrantedAuthority {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Override
  public String getAuthority() {
    return this.name;
  }
}
