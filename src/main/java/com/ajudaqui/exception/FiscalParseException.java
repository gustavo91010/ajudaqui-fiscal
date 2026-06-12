package com.ajudaqui.exception;

public class FiscalParseException extends RuntimeException {
    public FiscalParseException(String message) {
        super(message);
    }

    public FiscalParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
