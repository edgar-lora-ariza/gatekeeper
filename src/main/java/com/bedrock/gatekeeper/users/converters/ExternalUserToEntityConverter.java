package com.bedrock.gatekeeper.users.converters;

import com.bedrock.gatekeeper.users.entities.ExternalUserEntity;
import com.bedrock.gatekeeper.users.model.ExternalUser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ExternalUserToEntityConverter implements Converter<ExternalUser, ExternalUserEntity> {

  @Override
  public ExternalUserEntity convert(ExternalUser source) {
    ExternalUserEntity entity = new ExternalUserEntity();
    entity.setId(source.getId());
    entity.setEmail(source.getEmail());
    return entity;
  }
}
