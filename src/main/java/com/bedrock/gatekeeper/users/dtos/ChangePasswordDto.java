package com.bedrock.gatekeeper.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the data required to change a user's password.")
public record ChangePasswordDto(@Schema(description = "The user's current password.", example = "password123")
                                String oldPassword,
                                @Schema(description = "The user's new password.", example = "newPassword456")
                                String newPassword) {

}
