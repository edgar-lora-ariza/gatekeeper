package com.bedrock.gatekeeper.users.controllers;

import com.bedrock.gatekeeper.commons.model.SuperAdminAllowed;
import com.bedrock.gatekeeper.commons.model.SuperAdminAndAdminAllowed;
import com.bedrock.gatekeeper.users.dtos.CreateAccountDto;
import com.bedrock.gatekeeper.users.dtos.SetNewPasswordDto;
import com.bedrock.gatekeeper.users.usecases.ChangePasswordUseCase;
import com.bedrock.gatekeeper.users.usecases.CreateUserUseCase;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Observed
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

  private final CreateUserUseCase createUserUseCase;
  private final ChangePasswordUseCase changePasswordUseCase;

  @PostMapping
  @SuperAdminAllowed
  @Operation(summary = "Create a new admin account", description = "Private endpoint to create a new admin account.")
  @ApiResponse(responseCode = "204", description = "Admin account created successfully")
  @ApiResponse(responseCode = "400", description = "Bad request: Invalid password")
  @ApiResponse(responseCode = "409", description = "Admin account already exists")
  public ResponseEntity<Void> createAdminAccount(@RequestBody CreateAccountDto request) {
    createUserUseCase.createAdminUser(request);
    return ResponseEntity.noContent().build();
  }

  @SuperAdminAndAdminAllowed
  @PostMapping("/set-password")
  @Operation(summary = "Set a new password to an account", description = "Private endpoint to set a new password to an account.")
  @ApiResponse(responseCode = "204", description = "password set successfully")
  @ApiResponse(responseCode = "400", description = "Bad request")
  @ApiResponse(responseCode = "404", description = "Target account not found")
  public ResponseEntity<Void> setPasswordToAccount(@RequestBody SetNewPasswordDto request) {
    changePasswordUseCase.setPasswordToUser(request);
    return ResponseEntity.noContent().build();
  }
}
