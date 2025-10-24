package com.bedrock.gatekeeper.keys.ports;

import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import java.util.Optional;

public interface EncryptionKeyDataProvider {

  Optional<EncryptionKey> getEncryptionKey();

  void saveEncryptionKey(EncryptionKey encryptionKey);

  void deleteEncryptionKey();
}
