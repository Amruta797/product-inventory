package com.product.inventory.unit;

import com.product.inventory.product.Product;
import com.product.inventory.repositoty.ProductRepository;
import com.product.inventory.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    private Product product;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        product = new Product("Laptop", 0, 1200.00);
        product.setId(1L);
    }

    @Test
    public void testSaveProduct() {
        when(repository.save(product)).thenReturn(product);
        Product saved = service.createProduct(product);
        assertEquals("Laptop", saved.getName());
        verify(repository, times(1)).save(product);
    }

    @Test
    public void testDeleteById_Success() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        service.deleteProduct(1L);

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteById_ProductNotFound() {
        when(repository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.deleteProduct(1L));

        assertEquals("Product not found", exception.getMessage());
        verify(repository, times(1)).existsById(1L);
        verify(repository, times(0)).deleteById(any());
    }

    @Test
    public void testUpdateQuantity_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updated = service.updateQuantity(1L, 10);

        assertEquals(10, updated.getQuantity());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(updated);
    }

    @Test
    public void testUpdateQuantity_ProductNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.updateQuantity(1L, 10));

        assertEquals("Product not found", exception.getMessage());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(0)).save(any());
    }

    @Test
    public void testSearchByName_Found() {
        when(repository.findByNameContainingIgnoreCase("laptop"))
                .thenReturn(List.of(product));

        List<Product> result = service.searchByName("laptop");

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(repository, times(1)).findByNameContainingIgnoreCase("laptop");
    }

    @Test
    public void testSearchByName_EmptyList() {
        when(repository.findByNameContainingIgnoreCase("unknown"))
                .thenReturn(Collections.emptyList());

        List<Product> result = service.searchByName("unknown");

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByNameContainingIgnoreCase("unknown");
    }

    @Test
    public void testGetSummary() {
        List<Product> products = List.of(product);
        when(repository.findAll()).thenReturn(products);

        Map<String, Object> result = service.getInventorySummary();
        List<Object> outOfStockProducts = Collections.singletonList(result.get("outOfStock"));

        assertEquals(1, result.get("totalProducts"));
        assertEquals(0, result.get("totalQuantity"));
        assertEquals(1, outOfStockProducts.size());
        verify(repository, times(1)).findAll();
    }
}

