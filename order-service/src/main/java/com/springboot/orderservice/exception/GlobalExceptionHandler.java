package com.springboot.orderservice.exception;

import com.springboot.orderservice.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOrderNotFound(
            OrderNotFoundException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(
                        "Order not found",
                        "ORDER_NOT_FOUND",
                        List.of(ex.getMessage())
                )
        );
    }

    /**
     * Handles malformed JSON
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest().body(
                ApiResponse.error(
                        "Malformed JSON request",
                        "MALFORMED_JSON",
                        List.of(ex.getMessage())
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + " : " + e.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(
                ApiResponse.error(
                        "Validation failed",
                        "VALIDATION_ERROR",
                        errors
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        return ResponseEntity.internalServerError().body(
                ApiResponse.error(
                        "An unexpected error occurred",
                        "INTERNAL_ERROR",
                        List.of(ex.getMessage())
                )
        );
    }
}