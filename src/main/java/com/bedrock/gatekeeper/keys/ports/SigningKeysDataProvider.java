package com.white.label.gatekeeper.core.ports;

import com.white.label.gatekeeper.core.model.SigningKey;
import java.util.List;
import java.util.Optional;

public interface SigningKeysPort {

  SigningKey addKey(String keyIdentifier, String certificate, String privateKeyPem);

  Optional<String> inactivateKey(String id);

  Optional<SigningKey> getActiveKey();

  Optional<SigningKey> getKeyById(String id);

  Optional<SigningKey> getKeyByKeyIdentifier(String keyIdentifier);

  List<SigningKey> getAllKeys();

  void saveAll(List<SigningKey> signingKeys);
}
