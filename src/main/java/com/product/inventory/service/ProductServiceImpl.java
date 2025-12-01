package com.product.inventory.service;

import com.product.inventory.exception.ResourceNotFoundException;
import com.product.inventory.product.Product;
import com.product.inventory.repositoty.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public Product createProduct(Product product) {
        return repo.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    @Override
    public boolean existsById(Long id) {
        return repo.existsById(id);
    }

    @Override
    public List<Product> searchByName(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    @Override
    public Product updateQuantity(Long id, int quantity) {
        Product product = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setQuantity(quantity);
        return repo.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Map<String, Object> getInventorySummary() {
        List<Product> products = repo.findAll();

        int totalProducts = products.size();
        Map<String, Object> outOfStock = new HashMap<>();
        int totalQuantity = 0;
        double totalPrice = 0.0;

        for(Product product : products) {
            totalQuantity += product.getQuantity();
            totalPrice += product.getPrice();
            if (isOutOfStock(product)) {
                outOfStock.put("id", product.getId());
                outOfStock.put("name", product.getName());
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalProducts", totalProducts);
        summary.put("totalQuantity", totalQuantity);
        summary.put("averagePrice", getAveragePrice(totalPrice, totalProducts));
        summary.put("outOfStock", outOfStock);

        return summary;
    }

    private double getAveragePrice(double totalPrice, int totalProducts) {
        return totalPrice / totalProducts;
    }

    private boolean isOutOfStock(Product product) {
        return product.getQuantity() <= 0;
    }

}
