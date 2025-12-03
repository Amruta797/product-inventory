package com.product.inventory.repositoty;

import com.product.inventory.model.OutOfStockProduct;
import com.product.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT COUNT(p) FROM Product p")
    Long countProducts();

    @Query("SELECT SUM(p.quantity) FROM Product p")
    Long sumQuantities();

    @Query("SELECT AVG(p.price) FROM Product p")
    Double averagePrice();

    @Query("SELECT p.id AS id, p.name AS name FROM Product p WHERE p.quantity = 0")
    List<OutOfStockProduct> findByQuantity(int quantity);
}
