package com.bedrock.gatekeeper.users.converters;

import com.bedrock.gatekeeper.users.entities.AuthorityEntity;
import com.bedrock.gatekeeper.users.entities.UserEntity;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class UserToEntityConverter implements Converter<User, UserEntity> {

  @Override
  public UserEntity convert(User source) {
    UserEntity entity = new UserEntity();
    entity.setUsername(source.getUsername());
    entity.setPassword(source.getPassword());
    entity.setEnabled(source.isEnabled());
    entity.setAccountNonExpired(source.isAccountNonExpired());
    entity.setAccountNonLocked(source.isAccountNonLocked());
    entity.setCredentialsNonExpired(source.isCredentialsNonExpired());
    entity.setAuthorities(
        source.getAuthorities().stream()
            .map(
                grantedAuthority -> {
                  AuthorityEntity authorityEntity = new AuthorityEntity();
                  authorityEntity.setName(grantedAuthority.getAuthority());
                  return authorityEntity;
                })
            .collect(Collectors.toSet()));
    return entity;
  }
}
