package com.bedrock.gatekeeper.keys.ports;

import com.bedrock.gatekeeper.keys.model.SigningKey;
import java.util.List;
import java.util.Optional;

public interface SigningKeysDataProvider {

  SigningKey addKey(String keyIdentifier, String certificate, String privateKeyPem);

  Optional<SigningKey> getActiveKey();

  Optional<SigningKey> getKeyByKeyIdentifier(String keyIdentifier);

  List<SigningKey> getAllKeys();

  void saveAll(List<SigningKey> signingKeys);

  void deleteAll(List<SigningKey> signingKeys);
}
