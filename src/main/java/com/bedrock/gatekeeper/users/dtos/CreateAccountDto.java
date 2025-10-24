package com.bedrock.gatekeeper.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the data required to create a user's account.")
public record CreateAccountDto(@Schema(description = "The user's email.", example = "john_doe@example.com")
                               String email,
                               @Schema(description = "The user's password.", example = "password123")
                               String password,
                               @Schema(description = "The user's password confirmation.", example = "password123")
                               String passwordConfirmation) {
}
