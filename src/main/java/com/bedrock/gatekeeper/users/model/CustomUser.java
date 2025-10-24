package com.bedrock.gatekeeper.users.model;

import com.bedrock.gatekeeper.users.entities.UserEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
@Setter
public class CustomUser extends User {

  private final String id;

  public CustomUser(UserEntity user) {
    super(user.getUsername(), user.getPassword(), user.isEnabled(),
        user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(),
        user.getAuthorities());
    this.id = user.getId();
  }
  
  @JsonCreator
  public CustomUser(@JsonProperty("id") String id,
                    @JsonProperty("email") String username,
                    @JsonProperty("password") String password,
                    @JsonProperty("enabled") boolean enabled,
                    @JsonProperty("accountNonExpired") boolean accountNonExpired,
                    @JsonProperty("credentialsNonExpired") boolean credentialsNonExpired,
                    @JsonProperty("accountNonLocked") boolean accountNonLocked,
                    @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities) {
    super(username, (password != null) ? password : "[PROTECTED]", enabled, accountNonExpired, credentialsNonExpired,
        accountNonLocked, authorities);
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    CustomUser that = (CustomUser) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id);
  }
}
