package com.product.inventory.service;

import com.product.inventory.product.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {

    Product createProduct(Product product);

    List<Product> getAllProducts();

    boolean existsById(Long id);

    List<Product> searchByName(String name);

    Product updateQuantity(Long id, Integer quantity);

    void deleteProduct(Long id);

    Map<String, Object> getInventorySummary();

}
