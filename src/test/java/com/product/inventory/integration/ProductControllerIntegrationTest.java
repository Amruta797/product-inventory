package com.product.inventory.integration;

import com.product.inventory.TestPostgresContainer;
import com.product.inventory.product.Product;
import com.product.inventory.repositoty.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {

    @Container
    public static TestPostgresContainer postgres = TestPostgresContainer.getInstance();

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }

    @Test
    void testCreateProduct() throws Exception {
        Product product = new Product("Phone", 10, 500.00);

        ResultActions resultActions = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)));

        resultActions.andExpect(status().isCreated());
    }

    @Test
    void testCreateProduct_validationException() throws Exception {
        Product product = new Product("Phone", 10, 0.0);

        ResultActions result = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)));

        assertValidationException(result, "Price must be positive");
    }

    @Test
    void testGetAllProducts() throws Exception {
        repository.save(new Product("Laptop", 5, 1000.0));

        ResultActions result = mockMvc.perform(get("/products"));
        String response = result.andReturn().getResponse().getContentAsString();

        result.andExpect(status().isOk());
        assertThat(response.contains("Laptop")).isTrue();
    }

    @Test
    void testUpdateQuantity_success() throws Exception {
        Product saved = repository.save(new Product("TV", 3, 800.0));

        ResultActions result = mockMvc.perform(put("/products/" + saved.getId() + "/quantity?quantity=7"));
        String response = result.andReturn().getResponse().getContentAsString();

        result.andExpect(status().isOk());
        assertThat(response.contains("7")).isTrue();
    }

    @Test
    void testUpdateQuantity_productNotFound() throws Exception {
        ResultActions resultActions = mockMvc.perform(put("/products/" + 3000L + "/quantity?quantity=0"));
        assertValidationException(resultActions, "Product not found");
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product saved = repository.save(new Product("Camera", 2, 700.0));

        mockMvc.perform(delete("/products/" + saved.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteProduct_productNotFound() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/products/" + 3000L));
        assertValidationException(resultActions, "Product not found");
    }

    @Test
    void testInventorySummary() throws Exception {
        repository.save(new Product("Laptop", 0, 1000.0));

        ResultActions result = mockMvc.perform(get("/products/summary"));
        String response = result.andReturn().getResponse().getContentAsString();

        result.andExpect(status().isOk());
        assertThat(response.contains("outOfStock\":[{\"name\":\"Laptop\",\"id\":1}]")).isTrue();
    }

    private void assertValidationException(ResultActions resultActions, String msg) throws UnsupportedEncodingException {
        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertThatException();
        assertThat(response.contains(msg)).isTrue();
    }
}
