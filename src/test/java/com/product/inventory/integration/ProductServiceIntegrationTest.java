package com.product.inventory.integration;

import com.product.inventory.TestPostgresContainer;
import com.product.inventory.product.Product;
import com.product.inventory.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
public class ProductServiceIntegrationTest {

    @Container
    public static TestPostgresContainer postgres = TestPostgresContainer.getInstance();

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private ProductService service;

    @Test
    void testCreateProduct() {
        Product p = new Product("TV", 2, 900.0);
        Product saved = service.createProduct(p);
        assertThat(saved.getName()).isEqualTo(p.getName());
    }

    @Test
    void testUpdateQuantity_success() {
        int updatedQuantity = 5;
        Product p = new Product("Keyboard", 10, 100.0);
        Product saved = service.createProduct(p);
        Product updated = service.updateQuantity(saved.getId(), updatedQuantity);
        assertThat(updated.getQuantity()).isEqualTo(updatedQuantity);
    }

    @Test
    void testUpdateQuantity_productNotFound() {
        int updatedQuantity = 5;
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.updateQuantity(30000L, updatedQuantity));
        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void testDelete_success() {
        Product p = new Product("CPU", 100, 1000.0);
        service.createProduct(p);
        service.deleteProduct(p.getId());
        assertThatNoException();
    }

    @Test
    void testDelete_productNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.deleteProduct(30000L));
        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void testSearchByName() {
        service.createProduct(new Product("Washing machine", 100, 1000.0));
        service.createProduct(new Product("machine", 20, 900.0));
        service.createProduct(new Product("Lathe Machine", 10, 800.0));
        List<Product> matchingProducts = service.searchByName("machine");
        assertThat(matchingProducts.size()).isEqualTo(3);
    }

    @Test
    void testGetSummary() {
        service.createProduct(new Product("Refrigerator", 0, 1000.0));
        Map<String, Object> summary = service.getInventorySummary();

        List<Object> outOfStockProducts = Collections.singletonList(summary.get("outOfStock"));
        assertThat(outOfStockProducts.size()).isEqualTo(1);
    }
}
