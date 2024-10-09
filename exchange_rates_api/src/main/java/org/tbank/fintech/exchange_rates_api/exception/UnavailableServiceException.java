package org.tbank.fintech.exchange_rates_api.exception;

public class UnavailableServiceException extends RuntimeException {
    public UnavailableServiceException(String message) {
        super(message);
    }
}
