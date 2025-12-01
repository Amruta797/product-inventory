package com.product.inventory.repositoty;

import com.product.inventory.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    public List<Product> findByNameContainingIgnoreCase(String name);
}
