package com.manu.ms.order.config;

import com.manu.ms.order.exception.OutOfStockException;
import com.manu.ms.order.exception.NotFoundException;
import com.manu.ms.order.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.UUID;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStock(OutOfStockException ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("OutOfStockException: {} | traceId={}", ex.getMessage(), traceId);
        ErrorResponse error = ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.CONFLICT.value()))
                .message(ex.getMessage())
                .timestamp(Instant.now().toString())
                .traceId(traceId)
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String traceId = UUID.randomUUID().toString();
        log.warn("Validation failed: {} | traceId={}", ex.getMessage(), traceId);
        ErrorResponse error = ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .message("Validation error")
                .details(ex.getBindingResult().toString())
                .timestamp(Instant.now().toString())
                .traceId(traceId)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("NotFoundException: {} | traceId={}", ex.getMessage(), traceId);
        ErrorResponse error = ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .message(ex.getMessage())
                .timestamp(Instant.now().toString())
                .traceId(traceId)
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.error("Unhandled exception: {} | traceId={}", ex.getMessage(), traceId, ex);
        ErrorResponse error = ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .message("An unexpected error occurred. Please try again later.")
                .timestamp(Instant.now().toString())
                .traceId(traceId)
                .build();
        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
