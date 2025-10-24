package com.bedrock.gatekeeper.users.converters;

import com.bedrock.gatekeeper.users.entities.UserEntity;
import com.bedrock.gatekeeper.users.model.CustomUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EntityToCustomUserConverter implements Converter<UserEntity, CustomUser> {

  @Override
  public CustomUser convert(@NotNull UserEntity source) {
    return new CustomUser(source);
  }
}
