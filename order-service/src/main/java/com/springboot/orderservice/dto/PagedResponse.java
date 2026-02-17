package com.springboot.orderservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PagedResponse<T> {

    private List<T> content;

    private int pageNumber;
    private int pageSize;

    private long totalElements;
    private int totalPages;
}