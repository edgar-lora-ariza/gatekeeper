package com.bedrock.gatekeeper.commons.model;

public class ErrorCodes {

  public static final String UNAUTHORIZED = "ERROR-AUTH-000";
  public static final String INVALID_PASSWORD = "ERROR-AUTH-001";
  public static final String CLIENT_ALREADY_EXISTS = "ERROR-AUTH-002";
  public static final String CLIENT_NOT_FOUND = "ERROR-AUTH-003";
  public static final String USER_CAN_NOT_REMOVE_REQUIRED_AUTHORITY = "ERROR-AUTH-004";
  public static final String USER_CAN_NOT_UNAUTHORIZE_SUPER_USER = "ERROR-AUTH-005";
  public static final String USER_CAN_NOT_UNAUTHORIZE_USER = "ERROR-AUTH-006";
  public static final String USER_ALREADY_EXISTS = "ERROR-AUTH-007";
  public static final String USER_DOES_NOT_EXISTS = "ERROR-AUTH-008";
  public static final String INTERNAL_SERVER_ERROR = "ERROR-AUTH-500";


  private ErrorCodes() {

  }

}
