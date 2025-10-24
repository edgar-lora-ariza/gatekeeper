package com.bedrock.gatekeeper.keys.converters;

import com.bedrock.gatekeeper.keys.entities.SigningKeyEntity;
import com.bedrock.gatekeeper.keys.model.SigningKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SigningKeyToEntityConverter implements Converter<SigningKey, SigningKeyEntity> {

  @Override
  public SigningKeyEntity convert(SigningKey source) {
    if (source == null) {
      return null;
    }
    return new SigningKeyEntity(source.id(), source.keyIdentifier(), source.certificate(),
        source.privateKey(),
        true, null, null);
  }
}