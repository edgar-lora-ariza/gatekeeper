package com.bedrock.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(description = "Data transfer object for setting or updating a user's authorities (roles).")
public record SetAuthoritiesDto(
    @Schema(description = "The username of the user whose authorities will be updated.", example = "jane.doe", requiredMode = Schema.RequiredMode.REQUIRED)
    String username,

    @Schema(description = "The complete new set of authorities for the user. This will replace all existing authorities.", example = "[\"ROLE_USER\", \"ROLE_EDITOR\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    Set<String> authorities
) {}
