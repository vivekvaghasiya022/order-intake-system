package com.springboot.orderservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderRequest {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Customer email is required")
    private String customerEmail;
    @NotBlank(message = "Product code is required")
    private String productCode;
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity should be positive")
    private Integer quantity;
}
