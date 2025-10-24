package com.bedrock.gatekeeper.users.converters;

import com.bedrock.gatekeeper.users.entities.UserEntity;
import com.bedrock.gatekeeper.users.model.CustomUser;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class EntityToUserConverter implements Converter<UserEntity, CustomUser> {

  @Override
  public CustomUser convert(UserEntity source) {
    return new CustomUser(source);
  }
}
