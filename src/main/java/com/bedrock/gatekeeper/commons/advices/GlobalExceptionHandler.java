package com.bedrock.gatekeeper.commons.advices;

import com.bedrock.gatekeeper.commons.exceptions.BusinessException;
import com.bedrock.gatekeeper.commons.model.ErrorCodes;
import java.net.URI;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String ERROR_CODE = "errorCode";
  private static final String TIMESTAMP = "timestamp";
  private static final String ERRORS = "/errors/";

  @ExceptionHandler(BusinessException.class)
  public ProblemDetail handleBusinessException(BusinessException ex) {
    HttpStatus status = switch (ex.getErrorCode()) {
      case ErrorCodes.CLIENT_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case ErrorCodes.CLIENT_ALREADY_EXISTS, ErrorCodes.USER_ALREADY_EXISTS -> HttpStatus.CONFLICT;
      default -> HttpStatus.BAD_REQUEST;
    };

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    problemDetail.setTitle(status.getReasonPhrase());
    problemDetail.setType(URI.create(ERRORS + ex.getErrorCode()));
    problemDetail.setProperty(ERROR_CODE, ex.getErrorCode());
    problemDetail.setProperty(TIMESTAMP, Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle(HttpStatus.NOT_FOUND.getReasonPhrase());
    problemDetail.setType(URI.create(ERRORS + ErrorCodes.USER_DOES_NOT_EXISTS));
    problemDetail.setProperty(ERROR_CODE, ErrorCodes.USER_DOES_NOT_EXISTS);
    problemDetail.setProperty(TIMESTAMP, Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ProblemDetail handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    problemDetail.setTitle(HttpStatus.FORBIDDEN.getReasonPhrase());
    problemDetail.setType(URI.create(ERRORS + ErrorCodes.UNAUTHORIZED));
    problemDetail.setProperty(ERROR_CODE, ErrorCodes.UNAUTHORIZED);
    problemDetail.setProperty(TIMESTAMP, Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUncaughtException(Exception ex) {
    log.error("An unexpected error occurred: {}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred");
    problemDetail.setTitle("Internal Server Error");
    problemDetail.setType(URI.create("/errors/internal-server-error"));
    problemDetail.setProperty(ERROR_CODE, ErrorCodes.INTERNAL_SERVER_ERROR);
    problemDetail.setProperty(TIMESTAMP, Instant.now());
    return problemDetail;
  }
}
