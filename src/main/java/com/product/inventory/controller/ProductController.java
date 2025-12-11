package com.product.inventory.controller;

import com.product.inventory.dto.ProductRequest;
import com.product.inventory.dto.ProductResponse;
import com.product.inventory.dto.UpdateQuantityRequest;
import com.product.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@Validated
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // POST /products – Add new product to the inventory
    @Operation(summary = "Create a new product")
    @ApiResponses(value =
            {@ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Validation error")})
    @PostMapping
    public ResponseEntity<ProductResponse> addNewProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    // GET /products – Get all products in inventory
    @Operation(summary = "Get list of all Products in pages in Inventory")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List is Returned")})
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    // GET /products/search?name=xyz – Search products by name (case-insensitive)
    @Operation(summary = "Search products by name (case-insensitive)")
    @ApiResponses(value =
            {@ApiResponse(responseCode = "200", description = "Search is successful"),
            @ApiResponse(responseCode = "400", description = "Name must not be blank")})
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProduct(@RequestParam
                           @NotBlank(message = "Name must not be blank") String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }

    @Operation(summary = "Update quantity for given product")
    @ApiResponses(value =
            {@ApiResponse(responseCode = "200", description = "Update is successful"),
                    @ApiResponse(responseCode = "400", description = "Quantity must be greater than or equal to 0"),
                    @ApiResponse(responseCode = "404", description = "Product not found")})
    @PutMapping("/{id}/quantity")
    public ResponseEntity<ProductResponse> updateQuantity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuantityRequest request
    ) {
        ProductResponse updated = productService.updateQuantity(id, request.quantity());
        return ResponseEntity.ok(updated);
    }

    // DELETE /products/{id} – Delete product
    @Operation(summary = "Delete product")
    @ApiResponses(value =
            {@ApiResponse(responseCode = "204", description = "Deletion Successful")})
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // GET /products/summary – Inventory statistics
    @Operation(summary = "Get Inventory statistics")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Summary is Returned")})
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(productService.getInventorySummary());
    }
}
