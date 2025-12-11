package com.product.inventory.service;

import com.product.inventory.dto.ProductRequest;
import com.product.inventory.dto.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);

    List<ProductResponse> getAllProducts(Pageable pageable);

    boolean existsById(Long id);

    List<ProductResponse> searchByName(String name);

    ProductResponse updateQuantity(Long id, Integer quantity);

    void deleteProduct(Long id);

    Map<String, Object> getInventorySummary();

}
