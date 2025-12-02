package com.product.inventory.controller;

import com.product.inventory.exception.ResourceNotFoundException;
import com.product.inventory.product.Product;
import com.product.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })

    @PostMapping
    public ResponseEntity<Product> addNewProduct(@Valid @RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(summary = "Get list of all Products in Inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List is Returned")
    })
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Search products by name (case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search is successful"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    // GET /products/search?name=xyz – Search products by name (case-insensitive)
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProduct(@Valid @RequestParam String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }

    @Operation(summary = "Update quantity for given product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update is successful"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    // PUT /products/{id}/quantity – Update product quantity
    @PutMapping("/{id}/quantity")
    public ResponseEntity<Product> updateProductQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        if (productService.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        Product updated = productService.updateQuantity(id, quantity);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete is successful"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    // DELETE /products/{id} – Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        if (productService.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted Successfully");

    }

    @Operation(summary = "Get Inventory statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary is Returned")
    })
    // GET /products/summary – Inventory statistics
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(productService.getInventorySummary());
    }
}
