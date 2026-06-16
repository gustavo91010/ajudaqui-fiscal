package com.ajudaqui.exception;

public class FiscalParseException extends RuntimeException {
  public FiscalParseException(String message) {
    super(message);
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }

  @Override
  public String toString() {
    return getMessage();
  }
}
