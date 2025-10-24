package com.bedrock.gatekeeper.keys.converters;

import com.bedrock.gatekeeper.keys.entities.EncryptionKeyEntity;
import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EncryptionKeyToEntityConverter implements Converter<EncryptionKey, EncryptionKeyEntity> {

  @Override
  public EncryptionKeyEntity convert(EncryptionKey source) {
    EncryptionKeyEntity entity = new EncryptionKeyEntity();
    entity.setEncryptionKey(source.encryptionKey());

    return entity;
  }
}