package com.product.inventory.repositoty;

import com.product.inventory.dto.InventoryStats;
import com.product.inventory.dto.OutOfStockProduct;
import com.product.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    // In repository - direct DTO instantiation
    @Query("SELECT new com.product.inventory.dto.OutOfStockProduct(p.id, p.name) " +
            "FROM Product p WHERE p.quantity = 0")
    List<OutOfStockProduct> findByQuantity(int quantity);

    // Single query for all stats
    @Query("""
    SELECT
        COUNT(p) as totalProducts,
        COALESCE(SUM(p.quantity), 0) as totalQuantity,
        COALESCE(AVG(p.price), 0) as averagePrice
    FROM Product p
    """)
    InventoryStats getInventoryStatistics();
}
