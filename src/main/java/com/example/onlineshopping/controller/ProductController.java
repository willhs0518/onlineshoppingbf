package com.example.onlineshopping.controller;

import com.example.onlineshopping.domain.hibernate.ProductHibernate;
import com.example.onlineshopping.dto.OrderItemDTO;
import com.example.onlineshopping.dto.ProductMostDTO;
import com.example.onlineshopping.dto.ProductProfitDTO;
import com.example.onlineshopping.service.AdminService;
import com.example.onlineshopping.service.HomeService;
import com.example.onlineshopping.service.OrderService;
import com.example.onlineshopping.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class ProductController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private HomeService homeService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SummaryService summaryService;

    // Helper method to check if user has admin role
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_0"));
    }

    // Fetch all products - shared endpoint
    @GetMapping("/products/all")
    public ResponseEntity<?> getAllProducts(Authentication authentication) {
        if (isAdmin(authentication)) {
            // Admin sees all products including out-of-stock
            return ResponseEntity.ok(adminService.getAllProducts());
        } else {
            // Users only see in-stock products
            return ResponseEntity.ok(homeService.getAvailableProducts());
        }
    }

    // Fetch product details - shared endpoint
    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProductDetails(@PathVariable Long productId, Authentication authentication) {
        if (isAdmin(authentication)) {
            // Admin sees complete product details
            return ResponseEntity.ok(adminService.getProductById(productId));
        } else {
            // User sees limited product details
            return ResponseEntity.ok(homeService.getProductDetails(productId));
        }
    }

    // Update product details - admin only
    @PatchMapping("/products/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody ProductHibernate product,
                                           Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        // Ensure the ID in the path matches the product
        product.setProductId(productId);
        try {
            return ResponseEntity.ok(adminService.updateProduct(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Add a new product - admin only
    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductHibernate product, BindingResult result,
                                        Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        if (result.hasErrors()) {
            // Extract validation errors
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            ProductHibernate savedProduct = adminService.addProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Admin-specific stats endpoints
    @GetMapping("/products/profit/{count}")
    public ResponseEntity<?> getMostProfitableProducts(@PathVariable int count, Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        ProductProfitDTO mostProfitableProduct = summaryService.getMostProfitableProduct();
        if (mostProfitableProduct == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(mostProfitableProduct);
    }

    // Get top popular products
    @GetMapping("/products/popular/{count}")
    public ResponseEntity<?> getMostPopularProducts(@PathVariable int count, Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        List<ProductMostDTO> topSellingProducts = summaryService.getTopSellingProducts();
        if (topSellingProducts.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(topSellingProducts);
    }

    // Get total amount of products sold
    @GetMapping("/products/total-sold")
    public ResponseEntity<?> getTotalItemsSold(Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        Long totalSold = summaryService.getTotalItemsSold();
        return ResponseEntity.ok(totalSold);
    }

    // User-specific stats endpoints
    @GetMapping("/products/frequent/{count}")
    public ResponseEntity<?> getTopFrequentItems(@PathVariable int count, Authentication authentication) {
        String username = authentication.getName();
        List<OrderItemDTO> items = orderService.getTopFrequentItems(username);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/products/recent/{count}")
    public ResponseEntity<?> getTopRecentItems(@PathVariable int count, Authentication authentication) {
        String username = authentication.getName();
        List<OrderItemDTO> items = orderService.getTopRecentItems(username);
        return ResponseEntity.ok(items);
    }

}