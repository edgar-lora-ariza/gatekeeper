package com.bedrock.gatekeeper.keys.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to save or update the encryption key.")
public class SaveEncryptionKeyRequest {

  @NotBlank
  @Schema(description = "The encryption key, typically a UUID or a long random string.", example = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")
  private String encryptionKey;
}