package com.product.inventory.unit.repository;

import com.product.inventory.dto.InventoryStats;
import com.product.inventory.dto.OutOfStockProduct;
import com.product.inventory.entity.Product;
import com.product.inventory.repositoty.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product createProduct(String name, int quantity, BigDecimal price) {
        Product p = new Product(name, quantity, price);
        return productRepository.save(p);
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        createProduct("Laptop", 10, new BigDecimal(1000.0));
        createProduct("laptop cover", 5, new BigDecimal(20.0));
        createProduct("Mouse", 15, new BigDecimal(30.0));

        List<Product> result = productRepository.findByNameContainingIgnoreCase("LAPTOP");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("Laptop", "laptop cover");
    }

    @Test
    void testFindByQuantity_outOfStock() {
        createProduct("Keyboard", 0, new BigDecimal(50.0));
        createProduct("Monitor", 0, new BigDecimal(150.0));
        createProduct("Mouse", 10, new BigDecimal(25.0));

        List<OutOfStockProduct> outOfStock = productRepository.findByQuantity(0);

        assertThat(outOfStock).hasSize(2);
        assertThat(outOfStock).extracting(OutOfStockProduct::name)
                .containsExactlyInAnyOrder("Keyboard", "Monitor");
    }

    @Test
    void testGetInventoryStatistics() {
        createProduct("Laptop", 10, new BigDecimal(1000.0));
        createProduct("Mouse", 5, new BigDecimal(20.0));
        createProduct("Keyboard", 0, new BigDecimal(50.0));

        InventoryStats stats = productRepository.getInventoryStatistics();

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalProducts()).isEqualTo(3L);
        assertThat(stats.getTotalQuantity()).isEqualTo(15); // 10 + 5 + 0
        assertThat(stats.getAveragePrice()).isEqualByComparingTo(new BigDecimal("356.666666666667"));
    }

    @Test
    void testSaveAndRetrieve() {
        Product product = createProduct("Tablet", 7, new BigDecimal(299.99));

        Optional<Product> retrieved = productRepository.findById(product.getId());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Tablet");
        assertThat(retrieved.get().getQuantity()).isEqualTo(7);
        assertThat(retrieved.get().getPrice()).isEqualTo(new BigDecimal(299.99));
    }
}

