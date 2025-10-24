package com.bedrock.gatekeeper.keys.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to rotate the active signing key.")
public class RotateSigningKeyRequest {

  @NotBlank
  @Schema(description = "The new key identifier.", example = "new-key-id-2025")
  private String keyIdentifier;

  @NotBlank
  @Schema(description = "The new certificate in PEM format.", example = "-----BEGIN CERTIFICATE-----\\n...")
  private String certificate;

  @NotBlank
  @Schema(description = "The new private key in PEM format.", example = "-----BEGIN PRIVATE KEY-----\\n...")
  private String privateKey;
}