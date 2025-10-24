package com.bedrock.gatekeeper.keys.usecases;

import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import com.bedrock.gatekeeper.keys.ports.EncryptionKeyDataProvider;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

@Service
@Observed
public class SaveEncryptionKeyUseCase {

  private final EncryptionKeyDataProvider encryptionKeyPort;

  public SaveEncryptionKeyUseCase(EncryptionKeyDataProvider encryptionKeyPort) {
    this.encryptionKeyPort = encryptionKeyPort;
  }

  public void saveEncryptionKey(String encryptionKey) {
    encryptionKeyPort.deleteEncryptionKey();
    encryptionKeyPort.saveEncryptionKey(new EncryptionKey(encryptionKey));
  }
}
