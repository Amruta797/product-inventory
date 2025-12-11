package com.product.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateQuantityRequest(
        @Schema(description = "Quantity of the product. When 0, product is out of stock", example = "40")
        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity must be >= 0")
        Integer quantity
) {}
