package com.product.inventory.unit;

import com.product.inventory.dto.InventoryStats;

import java.math.BigDecimal;

public record TestInventoryStats(Long totalProducts,
                                 Integer totalQuantity,
                                 BigDecimal averagePrice) implements InventoryStats {
    @Override
    public Long getTotalProducts() {
        return totalProducts;
    }

    @Override
    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    @Override
    public BigDecimal getAveragePrice() {
        return averagePrice;
    }
}
