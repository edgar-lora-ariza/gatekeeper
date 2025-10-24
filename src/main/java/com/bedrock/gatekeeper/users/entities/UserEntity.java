package com.white.label.gatekeeper.infrastructure.data.providers.entities;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Set;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@Table(name = "users", schema = "auth")
public class UserEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private boolean enabled;

  @Column(name = "account_non_expired", nullable = false)
  private boolean accountNonExpired;

  @Column(name = "account_non_locked", nullable = false)
  private boolean accountNonLocked;

  @Column(name = "credentials_non_expired", nullable = false)
  private boolean credentialsNonExpired;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "user_authorities",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "authority_id"))
  private Set<AuthorityEntity> authorities;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }
}
