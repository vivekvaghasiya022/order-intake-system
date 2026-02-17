package com.springboot.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    public enum Status {
        @JsonProperty("SUCCESS") SUCCESS,
        @JsonProperty("FAILURE") FAILURE
    }

    private Status status;
    private String message;
    private String code;
    private T data;
    private List<String> errors;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private final Instant timestamp = Instant.now();

    // Success responses
    public static <T> ApiResponse<T> success(T data) {
        return success("Operation completed successfully", null, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return success(message, null, data);
    }

    public static <T> ApiResponse<T> success(String message, String code, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(Status.SUCCESS);
        response.setMessage(message);
        response.setCode(code);
        response.setData(data);
        response.setErrors(null);
        return response;
    }

    // Error responses
    public static <T> ApiResponse<T> error(String message) {
        return error(message, null, null, null);
    }

    public static <T> ApiResponse<T> error(String message, String code) {
        return error(message, code, null, null);
    }

    public static <T> ApiResponse<T> error(String message, String code, List<String> errors) {
        return error(message, code, null, errors);
    }

    public static <T> ApiResponse<T> error(String message, String code, T data, List<String> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(Status.FAILURE);
        response.setMessage(message);
        response.setCode(code);
        response.setData(data);
        response.setErrors(errors != null ? errors : Collections.emptyList());
        return response;
    }
}