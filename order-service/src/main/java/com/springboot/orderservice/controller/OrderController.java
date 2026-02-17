package com.springboot.orderservice.controller;

import com.springboot.orderservice.dto.ApiResponse;
import com.springboot.orderservice.dto.OrderRequest;
import com.springboot.orderservice.dto.PagedResponse;
import com.springboot.orderservice.model.Order;
import com.springboot.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @Valid @RequestBody OrderRequest request) {
        log.info("POST /api/orders - Creating order");
        Order response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Order created successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Order>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/orders - Fetching all orders");

        Page<Order> orderPage = orderService.getAllOrders(page, size);

        List<Order> content = orderPage.getContent();

        PagedResponse<Order> pagedResponse = PagedResponse.<Order>builder()
                .content(content)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.success("Orders fetched successfully", pagedResponse)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
        log.info("GET /api/orders/{} - Fetching order", id);
        Order response = orderService.getOrderById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Order fetched successfully for id: "+ id, response)
        );
    }
}
