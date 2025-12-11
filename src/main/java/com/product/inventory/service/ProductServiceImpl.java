package com.product.inventory.service;

import com.product.inventory.dto.InventoryStats;
import com.product.inventory.dto.ProductRequest;
import com.product.inventory.dto.ProductResponse;
import com.product.inventory.exception.ResourceNotFoundException;
import com.product.inventory.dto.OutOfStockProduct;
import com.product.inventory.entity.Product;
import com.product.inventory.repositoty.ProductRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles implementation logic for controller endpoints.
 * All methods are referenced directly by ProductController.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getQuantity(), p.getPrice());
    }

    /**
     * Adds new product to the Inventory
     *
     * @param productRequest : new product request
     * @return : response for newly created product
     */
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = productRequest.createProduct();
        return toResponse(repo.save(product));
    }

    /**
     * @return All products in inventory
     */
    @Override
    public List<ProductResponse> getAllProducts(Pageable pageable) {
        return repo.findAll(pageable).map(this::toResponse).getContent();
    }

    @Override
    public boolean existsById(Long id) {
        return !repo.existsById(id);
    }

    /**
     * Searches product by name. This is case-insensitive
     *
     * @param name : product name to be searched for
     */
    @Override
    public List<ProductResponse> searchByName(String name) {
        return repo.findByNameContainingIgnoreCase(name).stream().map(this::toResponse).toList();
    }

    /**
     * Updates quantity of a given product.
     * Throws Resource not found exception when product is not found
     *
     * @param id : id of the product to be updated
     */
    @Override
    public ProductResponse updateQuantity(Long id, Integer quantity) {
        Product product = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setQuantity(quantity);
        return toResponse(repo.save(product));
    }

    /**
     * Deletes product from inventory when id matches.
     * Throws Resource not found exception when product is not found
     *
     * @param id : id of the product to be deleted
     */
    @Override
    public void deleteProduct(Long id) {
        if (existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        repo.deleteById(id);
    }

    /**
     * Return inventory Summary in form of Map
     *
     * @return e.g {
     * "totalProducts": 5,
     * "totalQuantity": 78,
     * "averagePrice": 219.99,
     * "outOfStock": [
     * { "id": 3, "name": "Monitor" },
     * { "id": 5, "name": "Keyboard" }
     * ]
     * }
     */
    @Override
    public Map<String, Object> getInventorySummary() {
        List<OutOfStockProduct> outOfStock = repo.findByQuantity(0);
        InventoryStats stats = repo.getInventoryStatistics();
        return generateSummaryMap(stats, outOfStock);
    }

    /**
     * Creates summary map from given parameters
     */
    private Map<String, Object> generateSummaryMap(InventoryStats stats,
                                                   List<OutOfStockProduct> outOfStock) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalProducts", stats.getTotalProducts());
        summary.put("totalQuantity", stats.getTotalQuantity());
        summary.put("averagePrice", stats.getAveragePrice());
        summary.put("outOfStock", outOfStock);
        return summary;
    }

}
