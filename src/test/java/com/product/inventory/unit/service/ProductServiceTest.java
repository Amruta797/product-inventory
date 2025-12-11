package com.product.inventory.unit.service;

import com.product.inventory.dto.InventoryStats;
import com.product.inventory.dto.OutOfStockProduct;
import com.product.inventory.dto.ProductRequest;
import com.product.inventory.dto.ProductResponse;
import com.product.inventory.entity.Product;
import com.product.inventory.repositoty.ProductRepository;
import com.product.inventory.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        productRequest = new ProductRequest("Laptop", 10, new BigDecimal("999.99"));
        product = new Product(1L, "Laptop", 10, new BigDecimal("999.99"));
    }

    @Test
    public void testSaveProduct() {
        when(repository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = service.createProduct(productRequest);

        assertEquals(1L, response.id());
        assertEquals("Laptop", response.name());
        assertEquals(10, response.quantity());
        assertEquals(new BigDecimal("999.99"), response.price());
        verify(repository).save(any(Product.class));
    }

    @Test
    public void testGetProducts() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        List<Product> productList = List.of(
                new Product(1L, "Laptop", 10, new BigDecimal(1299.99)),
                new Product(2L, "Mouse", 5, new BigDecimal(20.99))
        );

        Page<Product> page = new PageImpl<>(productList, pageable, productList.size());

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        List<ProductResponse> allProducts = service.getAllProducts(pageable);

        assertEquals(2, allProducts.size());
        assertEquals(1L, allProducts.get(0).id());
        assertEquals("Laptop", allProducts.get(0).name());
        assertEquals("Mouse", allProducts.get(1).name());
        verify(repository).findAll(pageable);
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

        ProductResponse updated = service.updateQuantity(1L, 10);

        assertEquals(10, updated.quantity());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Product.class));
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
    public void testSearchByName_Success() {
        when(repository.findByNameContainingIgnoreCase("laptop"))
                .thenReturn(List.of(product));

        List<ProductResponse> result = service.searchByName("laptop");

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).name());
        verify(repository, times(1)).findByNameContainingIgnoreCase("laptop");
    }

    @Test
    public void testSearchByName_EmptyList() {
        when(repository.findByNameContainingIgnoreCase("unknown"))
                .thenReturn(Collections.emptyList());

        List<ProductResponse> result = service.searchByName("unknown");

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByNameContainingIgnoreCase("unknown");
    }

    @Test
    public void testGetSummary() {
        List<OutOfStockProduct> outOfStock = new ArrayList<>();
        outOfStock.add(new OutOfStockProduct(1L, "Laptop"));
        InventoryStats stats =
                new TestInventoryStats(1L, product.getQuantity(), product.getPrice());
        when(repository.getInventoryStatistics()).thenReturn(stats);
        when(repository.findByQuantity(0)).thenReturn(outOfStock);

        Map<String, Object> result = service.getInventorySummary();
        List<Object> outOfStockProducts = Collections.singletonList(result.get("outOfStock"));

        assertEquals(1L, result.get("totalProducts"));
        assertEquals(10, result.get("totalQuantity"));
        assertEquals(outOfStock.size(), outOfStockProducts.size());

        verify(repository, times(1)).findByQuantity(0);
        verify(repository, times(1)).getInventoryStatistics();
    }
}

