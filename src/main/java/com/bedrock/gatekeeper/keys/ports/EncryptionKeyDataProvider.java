package com.white.label.gatekeeper.core.ports;

import com.white.label.gatekeeper.core.model.EncryptionKey;
import java.util.Optional;

public interface EncryptionKeyPort {

  Optional<EncryptionKey> getEncryptionKey();

  void saveEncryptionKey(String encryptionKey);

  void deleteEncryptionKey();
}
