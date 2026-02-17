package com.springboot.orderservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderRequest {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Customer email is required")
    private String customerEmail;

    @NotBlank(message = "Product code is required")
    @Size(max = 100, message = "Product code must not exceed 100 characters")
    private String productCode;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity should be positive")
    private Integer quantity;
}
