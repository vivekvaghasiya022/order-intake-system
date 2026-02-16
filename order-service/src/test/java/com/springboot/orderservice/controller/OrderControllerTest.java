package com.springboot.orderservice.controller;

import com.springboot.orderservice.dto.OrderRequest;
import com.springboot.orderservice.dto.OrderResponse;
import com.springboot.orderservice.dto.OrderStatusEnum;
import com.springboot.orderservice.exception.OrderNotFoundException;
import com.springboot.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_validRequest_shouldReturn201() throws Exception {

        OrderRequest request = new OrderRequest("test@mail.com", "P100", 2);
        OrderResponse response = new OrderResponse(
                1L, "test@mail.com", "P100", 2,
                OrderStatusEnum.CREATED, Instant.now()
        );

        when(orderService.createOrder(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createOrder_invalidQuantity_shouldReturn400() throws Exception {

        OrderRequest request = new OrderRequest("test@mail.com", "P100", 0);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllOrders_shouldReturn200() throws Exception {

        when(orderService.getAllOrders()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderById_whenExists_shouldReturn200() throws Exception {

        OrderResponse response = new OrderResponse(
                1L, "test@mail.com", "P100", 2,
                OrderStatusEnum.CREATED, Instant.now()
        );

        when(orderService.getOrderById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderById_whenNotFound_shouldReturn404() throws Exception {

        when(orderService.getOrderById(1L))
                .thenThrow(new OrderNotFoundException(1L));

        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isNotFound());
    }
}
