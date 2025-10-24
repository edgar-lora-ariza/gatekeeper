package com.white.label.gatekeeper.application.use.cases.encryption.key;

import com.white.label.gatekeeper.core.model.EncryptionKey;
import com.white.label.gatekeeper.core.ports.EncryptionKeyPort;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class GetEncryptionKeyUseCase {

  private final EncryptionKeyPort encryptionKeyPort;

  public GetEncryptionKeyUseCase(EncryptionKeyPort encryptionKeyPort) {
    this.encryptionKeyPort = encryptionKeyPort;
  }

  public Optional<EncryptionKey> getEncryptionKey() {
    return encryptionKeyPort.getEncryptionKey();
  }
}
