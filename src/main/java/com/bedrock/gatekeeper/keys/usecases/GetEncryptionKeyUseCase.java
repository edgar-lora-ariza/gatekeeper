package com.bedrock.gatekeeper.keys.usecases;

import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import com.bedrock.gatekeeper.keys.ports.EncryptionKeyDataProvider;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Observed
public class GetEncryptionKeyUseCase {

  private final EncryptionKeyDataProvider encryptionKeyPort;

  public GetEncryptionKeyUseCase(EncryptionKeyDataProvider encryptionKeyPort) {
    this.encryptionKeyPort = encryptionKeyPort;
  }


  public Optional<EncryptionKey> getEncryptionKey() {
    Optional<EncryptionKey> encryptionKey = encryptionKeyPort.getEncryptionKey();
    log.info("Returning the encryption key");
    return encryptionKey;
  }
}
