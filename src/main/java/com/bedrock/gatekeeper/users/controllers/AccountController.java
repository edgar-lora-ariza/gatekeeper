package com.bedrock.gatekeeper.users.controllers;

import com.bedrock.gatekeeper.users.dtos.ChangePasswordDto;
import com.bedrock.gatekeeper.users.dtos.CreateAccountDto;
import com.bedrock.gatekeeper.users.dtos.CreatePasswordDto;
import com.bedrock.gatekeeper.users.usecases.ChangePasswordUseCase;
import com.bedrock.gatekeeper.users.usecases.CreateUserUseCase;
import com.bedrock.gatekeeper.users.usecases.DeleteUserUseCase;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Observed
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

  private final CreateUserUseCase createUserUseCase;
  private final ChangePasswordUseCase changePasswordUseCase;
  private final DeleteUserUseCase deleteUserUseCase;

  @PostMapping
  @Operation(summary = "Create a new account", description = "Public endpoint to create a new account.")
  @ApiResponse(responseCode = "204", description = "Account created successfully")
  @ApiResponse(responseCode = "400", description = "Bad request: Invalid password")
  @ApiResponse(responseCode = "409", description = "Account already exists")
  public ResponseEntity<Void> createAccount(@RequestBody CreateAccountDto request) {
    createUserUseCase.createUser(request);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/password")
  @Operation(summary = "Create a password for the account", description = "Public endpoint to create a new password.")
  @ApiResponse(responseCode = "204", description = "password successfully created")
  @ApiResponse(responseCode = "400", description = "Bad request: Invalid password")
  @ApiResponse(responseCode = "409", description = "password already exists")
  public ResponseEntity<Void> createPassword(@RequestBody CreatePasswordDto request) {
    changePasswordUseCase.createPassword(request);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/change-password")
  @Operation(summary = "Change the password", description = "Public endpoint to change the password.")
  @ApiResponse(responseCode = "204", description = "password changed successfully")
  @ApiResponse(responseCode = "400", description = "Bad request: Invalid password")
  public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDto request) {
    changePasswordUseCase.changePassword(request);
    return  ResponseEntity.noContent().build();
  }

  @DeleteMapping
  @Operation(summary = "Delete the account", description = "Public endpoint to delete the account.")
  @ApiResponse(responseCode = "204", description = "Account deleted successfully")
  public ResponseEntity<Void> deleteAccount() {
    deleteUserUseCase.deleteUser();
    return ResponseEntity.noContent().build();
  }
}
