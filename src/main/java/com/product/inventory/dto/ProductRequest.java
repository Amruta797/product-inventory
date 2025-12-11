package com.product.inventory.dto;

import com.product.inventory.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @Schema(description = "Name of the product", example = "Monitor")
        @NotNull(message = "Name can not be null")
        @NotBlank(message = "Name is required")
        String name,

        @Schema(description = "Quantity of the product. When 0, product is out of stock", example = "40")
        @NotNull(message = "Quantity can not be null")
        @Min(value = 0, message = "Quantity must be >= 0")
        Integer quantity,


        @Schema(description = "Price of the product.", example = "767.46")
        @NotNull(message = "Price can not be null")
        @DecimalMin("0.0")
        BigDecimal price
) {
    public Product createProduct() {
        return new Product(name, quantity, price);
    }

}
