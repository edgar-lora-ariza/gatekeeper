package com.bedrock.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for an administrator to set a new password for a specific user.")
public record SetNewPasswordDto(
    @Schema(description = "The username of the user whose password will be changed.", example = "jane.doe", requiredMode = Schema.RequiredMode.REQUIRED)
    String username,

    @Schema(description = "The new password to set for the user.", example = "N3wP@ssw0rd!", requiredMode = Schema.RequiredMode.REQUIRED)
    String newPassword
) {}
