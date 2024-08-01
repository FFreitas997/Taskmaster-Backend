package com.ffreitas.taskmaster.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException ex) {
        Set<String> errors = new HashSet<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> errors.add(((FieldError) error).getField() + ": " + error.getDefaultMessage()));
        return ResponseEntity
                .badRequest()
                .body(
                        ExceptionResponse
                                .builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(
                        ExceptionResponse
                                .builder()
                                .description("Invalid argument exception")
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse
                                .builder()
                                .description("An error occurred, please contact the system administrator")
                                .error(ex.getMessage())
                                .build()
                );
    }
}
