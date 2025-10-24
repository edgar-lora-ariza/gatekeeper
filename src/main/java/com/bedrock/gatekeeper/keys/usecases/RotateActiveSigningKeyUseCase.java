package com.bedrock.gatekeeper.keys.usecases;

import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import com.bedrock.gatekeeper.keys.model.SigningKey;
import com.bedrock.gatekeeper.keys.ports.SigningKeysDataProvider;
import com.bedrock.gatekeeper.keys.services.EncryptionService;
import io.micrometer.observation.annotation.Observed;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Observed
public class RotateActiveSigningKeyUseCase extends RotateUseCase {

  private static final List<String> DEPENDENT_BEANS = List.of("getActiveSigningKey", "getCertificate", "getPublicKey",
      "getPrivateKey", "jwkSource");

  private final SigningKeysDataProvider signingKeysDataProvider;
  private final EncryptionKey encryptionKey;
  private final EncryptionService encryptionService;

  public RotateActiveSigningKeyUseCase(ApplicationContext applicationContext,
                                       SigningKeysDataProvider signingKeysDataProvider,
                                       EncryptionKey encryptionKey,
                                       EncryptionService encryptionService) {
    super(applicationContext);
    this.signingKeysDataProvider = signingKeysDataProvider;
    this.encryptionKey = encryptionKey;
    this.encryptionService = encryptionService;
  }

  public SigningKey rotateActiveSigningKey(String keyIdentifier, String certificate, String privateKey) {
    try {
      SigningKey signingKey = this.signingKeysDataProvider.addKey(keyIdentifier,
          encryptionService.encrypt(certificate, encryptionKey.encryptionKey()),
          encryptionService.encrypt(privateKey, encryptionKey.encryptionKey()));

      refreshDependantBeans();

      return signingKey;
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
             BadPaddingException | InvalidAlgorithmParameterException e) {
      log.error("Error encrypting signing key: {}", e.getMessage(), e);
      throw new IllegalStateException("Error encrypting signing key", e);
    }
  }

  private void refreshDependantBeans() {
    DEPENDENT_BEANS.forEach(this::refreshBean);
  }
}
