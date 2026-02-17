package com.springboot.notificationservice.exception;

import com.springboot.notificationservice.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOrderNotFound(
            NotificationNotFoundException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(
                        "Notification not found",
                        "NOTIFICATION_NOT_FOUND",
                        List.of(ex.getMessage())
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error(
                        "Internal Server Error",
                        "INTERNAL_SERVER_ERROR",
                        List.of(ex.getMessage())
                )
        );
    }
}