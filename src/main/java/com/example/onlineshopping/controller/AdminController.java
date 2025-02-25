package com.example.onlineshopping.controller;

import com.example.onlineshopping.domain.hibernate.ProductHibernate;
import com.example.onlineshopping.dto.OrderDTO;
import com.example.onlineshopping.service.AdminService;
import com.example.onlineshopping.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/test")
    public ResponseEntity<String> testAdminEndpoint(Authentication authentication) {
        System.out.println("Request received - before security check");
        System.out.println("Current user: " + authentication.getName());
        System.out.println("Authorities: " + authentication.getAuthorities());
        return ResponseEntity.ok("Admin endpoint is working!");
    }

    // Fetch all orders
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    // Fetch order details
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(adminService.getOrderById(orderId));
    }

    // Complete order
    @PatchMapping("/orders/{orderId}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable Long orderId) {
        orderService.completeOrder(orderId);
        return ResponseEntity.ok("Order completed successfully");
    }

    // Fetch product listings
    @GetMapping("/products")
    public ResponseEntity<List<ProductHibernate>> getAllProducts() {
        return ResponseEntity.ok(adminService.getAllProducts());
    }

    // Fetch product details
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductHibernate> getProductDetails(@PathVariable Long productId) {
        return ResponseEntity.ok(adminService.getProductById(productId));
    }

    // Update product details
    @PatchMapping("/products")
    public ResponseEntity<ProductHibernate> updateProduct(@RequestBody ProductHibernate product) {
        return ResponseEntity.ok(adminService.updateProduct(product));
    }

    // Add a new product
    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductHibernate product, BindingResult result) {
        if (result.hasErrors()) {
            // Extract validation errors
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        ProductHibernate savedProduct = adminService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    // Cancel order
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            boolean isCanceled = orderService.cancelOrder(orderId);
            if (isCanceled) {
                return ResponseEntity.ok("Order canceled successfully.");
            } else {
                return ResponseEntity.badRequest().body("Order cannot be canceled.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
