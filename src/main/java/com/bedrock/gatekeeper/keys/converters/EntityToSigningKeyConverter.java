package com.bedrock.gatekeeper.keys.converters;

import com.bedrock.gatekeeper.keys.entities.SigningKeyEntity;
import com.bedrock.gatekeeper.keys.model.SigningKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EntityToSigningKeyConverter implements Converter<SigningKeyEntity, SigningKey> {

  @Override
  public SigningKey convert(SigningKeyEntity source) {
    if (source == null) {
      return null;
    }
    return new SigningKey(source.getId(), source.getKeyIdentifier(), source.getCertificate(), source.getPrivateKey());
  }
}