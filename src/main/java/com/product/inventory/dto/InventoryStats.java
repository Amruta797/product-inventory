package com.product.inventory.dto;

import java.math.BigDecimal;

// Projection interface for inventory stats
public interface InventoryStats {
    Long getTotalProducts();
    Integer getTotalQuantity();
    BigDecimal getAveragePrice();
}