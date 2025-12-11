package com.product.inventory.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        Integer quantity,
        BigDecimal price
) {}