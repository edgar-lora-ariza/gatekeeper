package com.bedrock.gatekeeper.keys.controllers;

import com.bedrock.gatekeeper.commons.model.SuperAdminAndAdminAllowed;
import com.bedrock.gatekeeper.keys.dtos.DeleteAllSigningKeysRequest;
import com.bedrock.gatekeeper.keys.dtos.RotateSigningKeyRequest;
import com.bedrock.gatekeeper.keys.dtos.SaveEncryptionKeyRequest;
import com.bedrock.gatekeeper.keys.model.EncryptionKey;
import com.bedrock.gatekeeper.keys.model.SigningKey;
import com.bedrock.gatekeeper.keys.usecases.DeleteAllSigningKeysUseCase;
import com.bedrock.gatekeeper.keys.usecases.GetActiveSigningKeyUseCase;
import com.bedrock.gatekeeper.keys.usecases.GetAllSigningKeysUseCase;
import com.bedrock.gatekeeper.keys.usecases.GetEncryptionKeyUseCase;
import com.bedrock.gatekeeper.keys.usecases.RotateActiveSigningKeyUseCase;
import com.bedrock.gatekeeper.keys.usecases.RotateEncryptionKeyUseCase;
import com.bedrock.gatekeeper.keys.usecases.SaveEncryptionKeyUseCase;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Observed
@RestController
@RequiredArgsConstructor
@SuperAdminAndAdminAllowed
@RequestMapping("/api/keys")
@Tag(name = "Keys", description = "Endpoints for managing cryptographic keys")
public class KeysController {

  private final GetActiveSigningKeyUseCase getActiveSigningKeyUseCase;
  private final GetAllSigningKeysUseCase getAllSigningKeysUseCase;
  private final GetEncryptionKeyUseCase getEncryptionKeyUseCase;
  private final RotateActiveSigningKeyUseCase rotateActiveSigningKeyUseCase;
  private final RotateEncryptionKeyUseCase rotateEncryptionKeyUseCase;
  private final SaveEncryptionKeyUseCase saveEncryptionKeyUseCase;
  private final DeleteAllSigningKeysUseCase deleteAllSigningKeysUseCase;

  @Operation(summary = "Get the active signing key", description = "Retrieves the currently active key used for signing tokens. If no key is active, it may initialize one based on configuration.")
  @ApiResponse(responseCode = "200", description = "Active signing key found")
  @GetMapping("/signing/active")
  public ResponseEntity<SigningKey> getActiveSigningKey() {
    SigningKey activeKey = getActiveSigningKeyUseCase.getActiveSigningKey();
    return ResponseEntity.ok(activeKey);
  }

  @Operation(summary = "Get all signing keys", description = "Retrieves a list of all signing keys stored in the system.")
  @ApiResponse(responseCode = "200", description = "List of signing keys")
  @GetMapping("/signing")
  public ResponseEntity<List<SigningKey>> getAllSigningKeys() {
    List<SigningKey> keys = getAllSigningKeysUseCase.getAllSigningKeys();
    return ResponseEntity.ok(keys);
  }

  @Operation(summary = "Rotate the active signing key", description = "Creates a new signing key, sets it as active, and deactivates the previous one.")
  @ApiResponse(responseCode = "200", description = "Signing key rotated successfully")
  @PostMapping("/signing/rotate")
  public ResponseEntity<SigningKey> rotateActiveSigningKey(@Valid @RequestBody RotateSigningKeyRequest request) {
    SigningKey newKey = rotateActiveSigningKeyUseCase.rotateActiveSigningKey(request.getKeyIdentifier(), request.getCertificate(), request.getPrivateKey());
    return ResponseEntity.ok(newKey);
  }

  @Operation(summary = "Deletes a list of signing keys", description = "Deletes a list of not active signing keys")
  @ApiResponse(responseCode = "200", description = "Signing keys deleted successfully")
  @DeleteMapping("/signing")
  public ResponseEntity<Void> deleteAllSigningKeys(@Valid @RequestBody DeleteAllSigningKeysRequest request) {
    deleteAllSigningKeysUseCase.deleteAllSigningKeys(request.signingKeys());
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Get the encryption key", description = "Retrieves the key used for encrypting sensitive data at rest, like private keys.")
  @ApiResponse(responseCode = "200", description = "Encryption key found")
  @ApiResponse(responseCode = "404", description = "Encryption key not found")
  @GetMapping("/encryption")
  public ResponseEntity<EncryptionKey> getEncryptionKey() {
    return getEncryptionKeyUseCase.getEncryptionKey().map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @Operation(summary = "Save or update the encryption key", description = "Saves a new encryption key. This is a sensitive operation and typically done once during setup.")
  @ApiResponse(responseCode = "204", description = "Encryption key saved successfully")
  @PostMapping("/encryption")
  public ResponseEntity<Void> saveEncryptionKey(@Valid @RequestBody SaveEncryptionKeyRequest request) {
    saveEncryptionKeyUseCase.saveEncryptionKey(request.getEncryptionKey());
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Rotate the encryption key", description = "Generates a new encryption key and re-encrypts all signing keys with it. This is a highly sensitive and resource-intensive operation.")
  @ApiResponse(responseCode = "204", description = "Encryption key rotated successfully")
  @PostMapping("/encryption/rotate")
  public ResponseEntity<Void> rotateEncryptionKey() {
    rotateEncryptionKeyUseCase.rotateEncryptionKey();
    return ResponseEntity.noContent().build();
  }
}
