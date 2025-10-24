package com.white.label.gatekeeper.application.use.cases.encryption.key;

import com.white.label.gatekeeper.application.use.cases.RotateUseCase;
import com.white.label.gatekeeper.core.model.EncryptionKey;
import com.white.label.gatekeeper.core.model.SigningKey;
import com.white.label.gatekeeper.core.ports.EncryptionKeyPort;
import com.white.label.gatekeeper.core.ports.SigningKeysPort;
import com.white.label.gatekeeper.core.services.EncryptionService;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RotateEncryptionKeyUseCase extends RotateUseCase {

  private static final List<String> DEPENDENT_BEANS = List.of("GetActiveSigningKeyUseCase", "GetAllSigningKeysUseCase",
      "RotateActiveSigningKeyUseCase", "getEncryptionKey");

  private final SigningKeysPort signingKeysPort;
  private final EncryptionKeyPort encryptionKeyPort;
  private final EncryptionService encryptionService;

  public RotateEncryptionKeyUseCase(ApplicationContext applicationContext,
                                    SigningKeysPort signingKeysPort,
                                    EncryptionKeyPort encryptionKeyPort,
                                    EncryptionService encryptionService) {
    super(applicationContext);
    this.signingKeysPort = signingKeysPort;
    this.encryptionKeyPort = encryptionKeyPort;
    this.encryptionService = encryptionService;
  }

  @Transactional
  public void rotateEncryptionKey() {
    List<SigningKey> keys = signingKeysPort.getAllKeys();
    if (!keys.isEmpty()) {
      Optional<EncryptionKey> encryptionKeyOptional = encryptionKeyPort.getEncryptionKey();
      encryptionKeyOptional.ifPresent(currentEncryptionKey -> {
        String newEncryptionKey = UUID.randomUUID().toString();
        List<SigningKey> processedSigningKeys = keys.stream()
            .map(signingKey -> this.decryptSigningKey(signingKey, currentEncryptionKey))
            .map(signingKey -> this.encryptSigningKey(signingKey, newEncryptionKey))
            .toList();

        signingKeysPort.saveAll(processedSigningKeys);
        encryptionKeyPort.saveEncryptionKey(newEncryptionKey);

        refreshDependantBeans();
      });
    }
  }

  private SigningKey decryptSigningKey(SigningKey signingKey, EncryptionKey encryptionKey) {
    try {
      return new SigningKey(signingKey.id(),
          signingKey.keyIdentifier(),
          encryptionService.decrypt(signingKey.certificate(), encryptionKey.encryptionKey()),
          encryptionService.decrypt(signingKey.privateKey(), encryptionKey.encryptionKey()));
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
             BadPaddingException | InvalidAlgorithmParameterException e) {
      log.error("Error decrypting the signing keys: {}", e.getMessage(), e);
      throw new IllegalStateException("Error decrypting the signing keys", e);
    }
  }

  private SigningKey encryptSigningKey(SigningKey signingKey, String encryptionKey) {
    try {
      return new SigningKey(signingKey.id(),
          signingKey.keyIdentifier(),
          encryptionService.encrypt(signingKey.certificate(), encryptionKey),
          encryptionService.encrypt(signingKey.privateKey(), encryptionKey));
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
             BadPaddingException | InvalidAlgorithmParameterException e) {
      log.error("Error encrypting the signing keys: {}", e.getMessage(), e);
      throw new IllegalStateException("Error encrypting the signing keys", e);
    }
  }

  private void refreshDependantBeans() {
    DEPENDENT_BEANS.forEach(this::refreshBean);
  }
}
