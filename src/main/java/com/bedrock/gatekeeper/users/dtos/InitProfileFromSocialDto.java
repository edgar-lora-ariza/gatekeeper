package com.bedrock.profile.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for initializing a new profile from social login.")
public class InitProfileFromSocialDto {

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email should be in a valid format")
  @Schema(description = "The user's email address", example = "john.doe@example.com")
  private String email;
}
