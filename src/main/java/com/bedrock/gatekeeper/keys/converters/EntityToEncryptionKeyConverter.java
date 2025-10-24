package com.bedrock.gatekeeper.keys.converters;

import com.bedrock.gatekeeper.keys.entities.EncryptionKeyEntity;
import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EntityToEncryptionKeyConverter implements Converter<EncryptionKeyEntity, EncryptionKey> {

  @Override
  public EncryptionKey convert(EncryptionKeyEntity source) {
    if (source == null) {
      return null;
    }
    return new EncryptionKey(source.getEncryptionKey());
  }
}