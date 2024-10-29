package org.tbank.fintech.exception;

public class UnavailableServiceException extends RuntimeException {
    public UnavailableServiceException(String message) {
        super(message);
    }
}
