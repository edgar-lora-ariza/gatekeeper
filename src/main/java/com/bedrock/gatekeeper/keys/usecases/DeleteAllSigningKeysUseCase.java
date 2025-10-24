package com.bedrock.gatekeeper.keys.usecases;

import com.bedrock.gatekeeper.keys.model.SigningKey;
import com.bedrock.gatekeeper.keys.ports.SigningKeysDataProvider;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Optional;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@Observed
public class DeleteAllSigningKeysUseCase extends RotateUseCase {

  private static final List<String> DEPENDENT_BEANS = List.of("getActiveSigningKey", "getCertificate", "getPublicKey",
      "getPrivateKey", "jwkSource");

  private final SigningKeysDataProvider signingKeysDataProvider;

  public DeleteAllSigningKeysUseCase(ApplicationContext applicationContext,
                                     SigningKeysDataProvider signingKeysDataProvider) {
    super(applicationContext);
    this.signingKeysDataProvider = signingKeysDataProvider;
  }

  public void deleteAllSigningKeys(List<SigningKey> signingKeys) {
    Optional<SigningKey> activeKey = signingKeysDataProvider.getActiveKey();
    activeKey.ifPresent(signingKeys::remove);
    signingKeysDataProvider.deleteAll(signingKeys);

    refreshDependantBeans();
  }

  private void refreshDependantBeans() {
    DEPENDENT_BEANS.forEach(this::refreshBean);
  }
}
