package com.white.label.gatekeeper.core.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final String ErrorCode;

  public BusinessException(String message, String ErrorCode) {
    super(message);
    this.ErrorCode = ErrorCode;
  }

  public BusinessException(String message, Throwable cause, String ErrorCode) {
    super(message, cause);
    this.ErrorCode = ErrorCode;
  }
}
