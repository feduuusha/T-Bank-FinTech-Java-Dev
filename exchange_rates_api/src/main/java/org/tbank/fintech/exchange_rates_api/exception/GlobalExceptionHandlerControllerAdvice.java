package org.tbank.fintech.exchange_rates_api.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.tbank.fintech.exchange_rates_api.model.response.exception.ExceptionMessage;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandlerControllerAdvice {

    @ExceptionHandler({BadRequestException.class, ServletRequestBindingException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<?> handleBindException(Exception e) {
        return ResponseEntity.badRequest().body(new ExceptionMessage(400, e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionMessage(404, e.getMessage()));
    }


    @ExceptionHandler({UnavailableServiceException.class, CallNotPermittedException.class})
    public ResponseEntity<?> handleUnavailableServiceException(Exception e) {
        if (e instanceof CallNotPermittedException)
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers((headers) -> headers.set("Retry-After", "3600")).body(new ExceptionMessage(503, "At the moment, the service of the central bank is not available, try later"));
        else
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers((headers) -> headers.set("Retry-After", "3600")).body(new ExceptionMessage(503, e.getMessage()));
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<?> handleIllegalStateException(Exception e) {
        return ResponseEntity.internalServerError().body(new ExceptionMessage(500,  e.getMessage()));
    }
}
