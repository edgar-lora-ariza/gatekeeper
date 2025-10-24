package com.bedrock.gatekeeper.keys.converters;

import com.bedrock.gatekeeper.keys.entities.SigningKeyEntity;
import com.bedrock.gatekeeper.keys.model.SigningKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SigningKeyToEntityConverter implements Converter<SigningKey, SigningKeyEntity> {

  @Override
  public SigningKeyEntity convert(SigningKey source) {
    SigningKeyEntity entity = new SigningKeyEntity();
    entity.setId(source.id());
    entity.setKeyIdentifier(source.keyIdentifier());
    entity.setCertificate(source.certificate());
    entity.setPrivateKey(source.privateKey());

    return entity;
  }
}