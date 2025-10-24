package com.bedrock.profile.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Response Object for initializing a new profile from social login.")
public record InitProfileFromSocialResponseDto(@Schema(description = "The unique identifier of the profile", example = "22690699-bf2e-452c-9ffb-9f2e85a21")
                                               String id) {
}
