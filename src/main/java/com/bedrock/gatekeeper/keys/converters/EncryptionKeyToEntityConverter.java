package com.bedrock.gatekeeper.keys.converters;

import com.bedrock.gatekeeper.keys.entities.EncryptionKeyEntity;
import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EncryptionKeyToEntityConverter implements Converter<EncryptionKey, EncryptionKeyEntity> {

  @Override
  public EncryptionKeyEntity convert(EncryptionKey source) {
    if (source == null) {
      return null;
    }
    // createdAt is handled by @CreationTimestamp
    return new EncryptionKeyEntity(source.encryptionKey(), null);
  }
}