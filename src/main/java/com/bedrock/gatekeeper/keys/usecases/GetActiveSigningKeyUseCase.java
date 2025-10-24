package com.bedrock.gatekeeper.keys.usecases;

import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import com.bedrock.gatekeeper.keys.model.SigningKey;
import com.bedrock.gatekeeper.keys.ports.SigningKeysDataProvider;
import com.bedrock.gatekeeper.keys.props.InitSigningKeyProps;
import com.bedrock.gatekeeper.keys.services.EncryptionService;
import io.micrometer.observation.annotation.Observed;
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
@Observed
@EnableConfigurationProperties(InitSigningKeyProps.class)
public class GetActiveSigningKeyUseCase extends SigningKeyUseCase {

  private final InitSigningKeyProps initSigningKeyProps;
  private final SigningKeysDataProvider signingKeysDataProvider;
  private final EncryptionKey encryptionKey;
  private final EncryptionService encryptionService;

  public GetActiveSigningKeyUseCase(InitSigningKeyProps initSigningKeyProps,
                                    SigningKeysDataProvider signingKeysDataProvider,
                                    EncryptionService encryptionService,
                                    EncryptionKey encryptionKey) {
    super(encryptionKey, encryptionService);
    this.initSigningKeyProps = initSigningKeyProps;
    this.signingKeysDataProvider = signingKeysDataProvider;
    this.encryptionKey = encryptionKey;
    this.encryptionService = encryptionService;
  }

  public SigningKey getActiveSigningKey() {
    Optional<SigningKey> activeKeyOptional = signingKeysDataProvider.getActiveKey();
    if (activeKeyOptional.isPresent()) {
      SigningKey activeEncryptedSigningKey = activeKeyOptional.get();

      log.info("Returning the active signing key");
      return getDecryptedSigningKey(activeEncryptedSigningKey);
    }

    if (initSigningKeyProps.keyIdentifier().isBlank()) {
      log.error("Tokens signing identifier is missing");
      throw new IllegalStateException("Tokens signing identifier is missing");
    }

    String keyIdentifier = initSigningKeyProps.keyIdentifier();
    Optional<SigningKey> signingKeyOptional = signingKeysDataProvider.getKeyByKeyIdentifier(keyIdentifier);
    if (signingKeyOptional.isPresent()) {
      log.error("Tokens signing identifier already exists");
      throw new IllegalStateException("Tokens signing identifier already exists");
    }

    if (initSigningKeyProps.certificate().isBlank()) {
      log.error("Tokens signing certificate is missing");
      throw new IllegalStateException("Tokens signing certificate is missing");
    }

    if (initSigningKeyProps.privateKey().isBlank()) {
      log.error("Tokens signing private key is missing");
      throw new IllegalStateException("Tokens signing private key is missing");
    }

    try {
      log.info("Generating a new signing key");
      SigningKey encryptedSigningKey = signingKeysDataProvider.addKey(initSigningKeyProps.keyIdentifier(),
          encryptionService.encrypt(initSigningKeyProps.certificate(), encryptionKey.encryptionKey()),
          encryptionService.encrypt(initSigningKeyProps.privateKey(), encryptionKey.encryptionKey()));
      return getDecryptedSigningKey(encryptedSigningKey);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
             BadPaddingException | InvalidAlgorithmParameterException e) {
      log.error("Error encrypting signing key: {}", e.getMessage(), e);
      throw new IllegalStateException("Error encrypting signing key", e);
    }
  }
}
