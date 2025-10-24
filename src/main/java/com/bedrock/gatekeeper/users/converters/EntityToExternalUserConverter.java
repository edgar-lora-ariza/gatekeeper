package com.bedrock.gatekeeper.users.converters;

import com.bedrock.gatekeeper.users.entities.ExternalUserEntity;
import com.bedrock.gatekeeper.users.model.ExternalUser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EntityToExternalUserConverter implements Converter<ExternalUserEntity, ExternalUser> {

  @Override
  public ExternalUser convert(ExternalUserEntity source) {
    return ExternalUser.builder()
        .id(source.getId())
        .email(source.getEmail())
        .build();
  }
}
