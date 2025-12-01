package com.product.inventory.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
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

    public Product() {
    }

    public Product(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public Integer getQuantity() {return quantity;}
    public void setQuantity(Integer quantity) {this.quantity = quantity;}

    public Double getPrice() {return price;}
    public void setPrice(Double price) {this.price = price;}
}
