package com.white.label.gatekeeper.core.model;

public class ErrorCodes {

  private ErrorCodes() {

  }

  public static final String CLIENT_ALREADY_EXISTS = "ERROR-001";
  public static final String CLIENT_NOT_EXISTS = "ERROR-002";
  public static final String USER_ALREADY_EXISTS = "ERROR-003";
  public static final String USER_NOT_FOUND = "ERROR-004";
  public static final String INVALID_PASSWORD = "ERROR-005";
}
