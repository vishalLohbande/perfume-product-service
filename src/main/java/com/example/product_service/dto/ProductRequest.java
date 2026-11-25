package com.example.product_service.dto;
import java.math.BigDecimal;

public record ProductRequest(
        String name,
        BigDecimal price,
        Integer quantity
) {
}
