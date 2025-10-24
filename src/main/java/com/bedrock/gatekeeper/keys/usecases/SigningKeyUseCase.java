package com.white.label.gatekeeper.application.use.cases;

import com.white.label.gatekeeper.core.model.EncryptionKey;
import com.white.label.gatekeeper.core.model.SigningKey;
import com.white.label.gatekeeper.core.services.EncryptionService;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SigningKeyUseCase {

  private final EncryptionKey encryptionKey;
  private final EncryptionService encryptionService;

  protected SigningKeyUseCase(EncryptionKey encryptionKey, EncryptionService encryptionService) {
    this.encryptionKey = encryptionKey;
    this.encryptionService = encryptionService;
  }

  public SigningKey getDecryptedSigningKey(SigningKey signingKey) {
    try {
      return new SigningKey(signingKey.id(),
          signingKey.keyIdentifier(),
          encryptionService.decrypt(signingKey.certificate(), encryptionKey.encryptionKey()),
          encryptionService.decrypt(signingKey.privateKey(), encryptionKey.encryptionKey()));
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
             BadPaddingException | InvalidAlgorithmParameterException e) {
      log.error("Error decrypting signing key: {}", e.getMessage(), e);
      throw new IllegalStateException("Error decrypting signing key", e);
    }
  }
}
