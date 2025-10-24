package com.white.label.gatekeeper.application.use.cases.signing.keys;

import com.white.label.gatekeeper.application.use.cases.SigningKeyUseCase;
import com.white.label.gatekeeper.infrastructure.config.props.gatekeeper.GatekeeperProps;
import com.white.label.gatekeeper.core.model.EncryptionKey;
import com.white.label.gatekeeper.core.model.SigningKey;
import com.white.label.gatekeeper.core.ports.SigningKeysPort;
import com.white.label.gatekeeper.core.services.EncryptionService;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableConfigurationProperties(GatekeeperProps.class)
public class GetActiveSigningKeyUseCase extends SigningKeyUseCase {

  private final GatekeeperProps gatekeeperProps;
  private final SigningKeysPort signingKeysPort;
  private final EncryptionKey encryptionKey;
  private final EncryptionService encryptionService;

  public GetActiveSigningKeyUseCase(GatekeeperProps gatekeeperProps,
                                    SigningKeysPort signingKeysPort,
                                    EncryptionService encryptionService,
                                    EncryptionKey encryptionKey) {
    super(encryptionKey, encryptionService);
    this.gatekeeperProps = gatekeeperProps;
    this.signingKeysPort = signingKeysPort;
    this.encryptionKey = encryptionKey;
    this.encryptionService = encryptionService;
  }

  public SigningKey getActiveSigningKey() {
    Optional<SigningKey> activeKeyOptional = signingKeysPort.getActiveKey();
    if (activeKeyOptional.isPresent()) {
      SigningKey activeEncryptedSigningKey = activeKeyOptional.get();

      return getDecryptedSigningKey(activeEncryptedSigningKey);
    }

    if (gatekeeperProps.init().tokensSigning().keyIdentifier().isBlank()) {
      log.error("Tokens signing identifier is missing");
      throw new IllegalStateException("Tokens signing identifier is missing");
    }

    String keyIdentifier = gatekeeperProps.init().tokensSigning().keyIdentifier();
    Optional<SigningKey> signingKeyOptional = signingKeysPort.getKeyByKeyIdentifier(keyIdentifier);
    if (signingKeyOptional.isPresent()) {
      log.error("Tokens signing identifier already exists");
      throw new IllegalStateException("Tokens signing identifier already exists");
    }

    if (gatekeeperProps.init().tokensSigning().certificate().isBlank()) {
      log.error("Tokens signing certificate is missing");
      throw new IllegalStateException("Tokens signing certificate is missing");
    }

    if (gatekeeperProps.init().tokensSigning().privateKey().isBlank()) {
      log.error("Tokens signing private key is missing");
      throw new IllegalStateException("Tokens signing private key is missing");
    }

    try {
      SigningKey encryptedSigningKey = signingKeysPort.addKey(gatekeeperProps.init().tokensSigning().keyIdentifier(),
          encryptionService.encrypt(gatekeeperProps.init().tokensSigning().certificate(),
              encryptionKey.encryptionKey()),
          encryptionService.encrypt(gatekeeperProps.init().tokensSigning().privateKey(),
              encryptionKey.encryptionKey()));
      return getDecryptedSigningKey(encryptedSigningKey);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
             BadPaddingException | InvalidAlgorithmParameterException e) {
      log.error("Error encrypting signing key: {}", e.getMessage(), e);
      throw new IllegalStateException("Error encrypting signing key", e);
    }
  }
}
