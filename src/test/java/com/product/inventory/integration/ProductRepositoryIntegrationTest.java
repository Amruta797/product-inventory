package com.product.inventory.integration;

import com.product.inventory.TestPostgresContainer;
import com.product.inventory.model.Product;
import com.product.inventory.repositoty.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
public class ProductRepositoryIntegrationTest {

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
    private ProductRepository repository;

    @Test
    void testSaveAndFind() {
        Product p = new Product("Laptop", 4, new BigDecimal("1200.0"));
        Product saved = repository.save(p);

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).isPresent();
    }

    @Test
    void testFindByNameIgnoreCase() {
        repository.save(new Product("Laptop", 5, new BigDecimal("1200.0")));
        repository.save(new Product("laptop",6,  new BigDecimal("1100.0")));

        List<Product> products = repository.findByNameContainingIgnoreCase("LAP");
        assertThat(products).hasSize(2);
    }

    @Test
    void testSaveAndDelete() {
        Product p = new Product("Monitor", 10, new BigDecimal("120.0"));
        Product saved = repository.save(p);

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).isPresent();

        repository.deleteById(p.getId());
        assertThat(repository.findById(p.getId()).isPresent()).isFalse();
    }

    @Test
    void testUpdate() {
        Product p = new Product("Laptop", 10, new BigDecimal("120.0"));
        repository.save(p);
        p.setQuantity(0);
        repository.save(p);

        Optional<Product> updated = repository.findById(p.getId());
        assertThat(updated).isPresent();
        assertEquals(0, updated.get().getQuantity());
    }
}
