package com.product.inventory.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NotNull
@Entity
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Name of the product", example = "Monitor", required = true)
    @NotNull(message = "Name can not be null")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Quantity of the product. When 0, product is out of stock", example = "40", required = true)
    @NotNull(message = "Quantity can not be null")
    @PositiveOrZero(message = "Quantity must be greater than or equal to 0")
    private Integer quantity;

    @Schema(description = "Price of the product.", example = "767.46", required = true)
    @NotNull(message = "Price can not be null")
    @Positive(message = "Price must be positive")
    private Double price;

    public Product(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

}
