package com.white.label.gatekeeper.application.use.cases.signing.keys;

import com.white.label.gatekeeper.application.use.cases.SigningKeyUseCase;
import com.white.label.gatekeeper.core.model.EncryptionKey;
import com.white.label.gatekeeper.core.model.SigningKey;
import com.white.label.gatekeeper.core.ports.SigningKeysPort;
import com.white.label.gatekeeper.core.services.EncryptionService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GetAllSigningKeysUseCase extends SigningKeyUseCase {

  private final SigningKeysPort signingKeysPort;

  public GetAllSigningKeysUseCase(SigningKeysPort signingKeysPort,
                                  EncryptionService encryptionService,
                                  EncryptionKey encryptionKey) {
    super(encryptionKey, encryptionService);
    this.signingKeysPort = signingKeysPort;
  }

  public List<SigningKey> getAllSigningKeys() {
    return signingKeysPort.getAllKeys().stream()
        .map(this::getDecryptedSigningKey)
        .toList();
  }
}
