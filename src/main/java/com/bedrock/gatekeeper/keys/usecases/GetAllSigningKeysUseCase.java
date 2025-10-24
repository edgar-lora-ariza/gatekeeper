package com.bedrock.gatekeeper.keys.usecases;

import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import com.bedrock.gatekeeper.keys.model.SigningKey;
import com.bedrock.gatekeeper.keys.ports.SigningKeysDataProvider;
import com.bedrock.gatekeeper.keys.services.EncryptionService;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Observed
public class GetAllSigningKeysUseCase extends SigningKeyUseCase {

  private final SigningKeysDataProvider signingKeysDataProvider;

  public GetAllSigningKeysUseCase(SigningKeysDataProvider signingKeysDataProvider,
                                  EncryptionService encryptionService,
                                  EncryptionKey encryptionKey) {
    super(encryptionKey, encryptionService);
    this.signingKeysDataProvider = signingKeysDataProvider;
  }

  public List<SigningKey> getAllSigningKeys() {
    List<SigningKey> signingKeyList = signingKeysDataProvider.getAllKeys().stream()
        .map(this::getDecryptedSigningKey)
        .toList();
    log.info("Returning {} signing keys", signingKeyList.size());
    return signingKeyList;
  }
}
