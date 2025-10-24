package com.bedrock.profile.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for initializing a new profile.")
public class InitProfileDto {
  
  @NotBlank(message = "Id cannot be blank")
  @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "ID must be a valid UUID")
  @Schema(description = "The unique identifier of the profile", example = "22690699-bf2e-452c-9ffb-9f2e85a21ea1")
  private String id;

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email should be in a valid format")
  @Schema(description = "The user's email address", example = "john.doe@example.com")
  private String email;
}
