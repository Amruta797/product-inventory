package com.product.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class OutOfStockProduct {
    private Long id;
    private String name;
}

