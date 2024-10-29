package org.tbank.fintech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandlerControllerAdvice {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler({BindException.class, UnsupportedOperationException.class})
    public ResponseEntity<?> handleBindException(Exception exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<?> handleOtherExceptions(Exception exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler({UnavailableServiceException.class})
    public ResponseEntity<?> handleOtherExceptions(UnavailableServiceException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
        return ResponseEntity.of(problemDetail).build();
    }
}
