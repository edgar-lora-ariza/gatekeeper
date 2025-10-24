package com.white.label.gatekeeper.application.use.cases.encryption.key;

import com.white.label.gatekeeper.core.ports.EncryptionKeyPort;
import org.springframework.stereotype.Service;

@Service
public class SaveEncryptionKeyUseCase {

  private final EncryptionKeyPort encryptionKeyPort;

  public SaveEncryptionKeyUseCase(EncryptionKeyPort encryptionKeyPort) {
    this.encryptionKeyPort = encryptionKeyPort;
  }

  public void saveEncryptionKey(String encryptionKey) {
    encryptionKeyPort.deleteEncryptionKey();
    encryptionKeyPort.saveEncryptionKey(encryptionKey);
  }
}
