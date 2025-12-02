package com.product.inventory.service;

import com.product.inventory.exception.ResourceNotFoundException;
import com.product.inventory.model.Product;
import com.product.inventory.repositoty.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    /**
     * Adds new product to the Inventory
     *
     * @param product : new product
     * @return : saved product
     */
    @Override
    public Product createProduct(Product product) {
        return repo.save(product);
    }

    /**
     * @return All products in inventory
     */
    @Override
    public List<Product> getAllProducts() {
        return repo.findAll();
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
    public List<Product> searchByName(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    /**
     * Updates quantity of a given product.
     * Throws Resource not found exception when product is not found
     *
     * @param id : id of the product to be updated
     */
    @Override
    public Product updateQuantity(Long id, Integer quantity) {
        Product product = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setQuantity(quantity);
        return repo.save(product);
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
        List<Product> products = repo.findAll();

        int totalProducts = products.size();
        List<Object> outOfStock = new ArrayList<>();

        int totalQuantity = 0;
        double totalPrice = 0.0;

        for (Product product : products) {
            totalQuantity += product.getQuantity();
            totalPrice += product.getPrice();
            populateOutOfStock(product, outOfStock);
        }

        return generateSummaryMap(totalProducts, totalQuantity, totalPrice, outOfStock);
    }

    /**
     * Average price of a product depending upon total price and total number of products
     */
    private double getAveragePrice(double totalPrice, int totalProducts) {
        return totalPrice / totalProducts;
    }

    /**
     * Returns true when Product quantity is 0, otherwise false
     */
    private boolean isOutOfStock(Product product) {
        return product.getQuantity() <= 0;
    }

    /**
     * Updates list of out of stock products. Product is out of stock when its quantity is 0
     */
    private void populateOutOfStock(Product product, List<Object> outOfStock) {
        if (isOutOfStock(product)) {
            Map<String, Object> outOfStockProduct = new HashMap<>();
            outOfStockProduct.put("id", product.getId());
            outOfStockProduct.put("name", product.getName());
            outOfStock.add(outOfStockProduct);
        }
    }

    /**
     * Creates summary map from given parameters
     */
    private Map<String, Object> generateSummaryMap(int totalProducts, int totalQuantity, double totalPrice, List<Object> outOfStock) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalProducts", totalProducts);
        summary.put("totalQuantity", totalQuantity);
        summary.put("averagePrice", getAveragePrice(totalPrice, totalProducts));
        summary.put("outOfStock", outOfStock);
        return summary;
    }

}
