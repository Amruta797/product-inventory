package com.product.inventory.unit.controller;

import com.product.inventory.controller.ProductController;
import com.product.inventory.dto.UpdateQuantityRequest;
import com.product.inventory.entity.Product;
import com.product.inventory.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddNewProduct_returns201() throws Exception {
        Product product = new Product("Laptop", 10, new BigDecimal(1299.99));
        Mockito.when(productService.createProduct(any(Product.class)))
                .thenReturn(product);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void testGetAllProducts_returns200() throws Exception {
        List<Product> list = List.of(
                new Product("Laptop", 10, new BigDecimal(1299.99)),
                new Product("Mouse", 5, new BigDecimal(20.99))
        );

        Mockito.when(productService.getAllProducts(any(PageRequest.class)))
                .thenReturn(list);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void testSearchProduct_returns200() throws Exception {
        List<Product> list = List.of(new Product("Laptop", 10, new BigDecimal(1299.99)));

        Mockito.when(productService.searchByName("Laptop"))
                .thenReturn(list);

        mockMvc.perform(get("/products/search")
                        .param("name", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testSearchProduct_blankName_returns400() throws Exception {
        mockMvc.perform(get("/products/search")
                        .param("name", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateQuantity_returns200() throws Exception {
        Product updated = new Product("Laptop", 20, new BigDecimal(1299.99));

        Mockito.when(productService.updateQuantity(eq(1L), eq(20)))
                .thenReturn(updated);

        UpdateQuantityRequest request = new UpdateQuantityRequest(20);

        mockMvc.perform(put("/products/1/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(20));
    }

    @Test
    void testUpdateQuantity_invalidQuantity_returns400() throws Exception {
        UpdateQuantityRequest request = new UpdateQuantityRequest(-5);

        mockMvc.perform(put("/products/1/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteProduct_returns204() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetSummary_returns200() throws Exception {
        Map<String, Object> summary = Map.of(
                "totalProducts", 5,
                "totalQuantity", 100,
                "averagePrice", new BigDecimal("99.99")
        );

        Mockito.when(productService.getInventorySummary()).thenReturn(summary);

        mockMvc.perform(get("/products/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(5));
    }
}

