package org.tbank.fintech.lesson_9.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandlerRestControllerAdvice {

    @ExceptionHandler({BindException.class, EntityNotFoundException.class, ValidationException.class})
    public ResponseEntity<?> handleBindException(HttpServletRequest req, Exception e) {
        return ResponseEntity.badRequest().body(new ExceptionMessage(Timestamp.from(Instant.now()), 400, e.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<?> handleNoSuchElementException(HttpServletRequest req, NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionMessage(Timestamp.from(Instant.now()), 404, e.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<?> handleIllegalStateException(HttpServletRequest req, Exception e) {
        return ResponseEntity.internalServerError().body(new ExceptionMessage(Timestamp.from(Instant.now()), 500, e.getMessage(), req.getRequestURI()));
    }
}
