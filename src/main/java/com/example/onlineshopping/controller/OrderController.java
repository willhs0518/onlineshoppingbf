package com.example.onlineshopping.controller;

import com.example.onlineshopping.dto.OrderDTO;
import com.example.onlineshopping.dto.OrderRequestDTO;
import com.example.onlineshopping.service.AdminService;
import com.example.onlineshopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AdminService adminService;

    // Helper method to check if user has admin role
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_0"));
    }

    // Place a new order
    @PostMapping("/orders")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequestDTO orderRequest, Authentication authentication) {
        String username = authentication.getName();
        try {
            Long orderId = orderService.placeOrder(username, orderRequest.getOrder());
            return ResponseEntity.ok("Order created in Processing status. Order ID: " + orderId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all orders
    @GetMapping("/orders/all")
    public ResponseEntity<?> getAllOrders(Authentication authentication) {
        if (isAdmin(authentication)) {
            // Admin can see all orders
            return ResponseEntity.ok(adminService.getAllOrders());
        } else {
            // Regular user can only see their own orders
            String username = authentication.getName();
            return ResponseEntity.ok(orderService.getUserOrders(username));
        }
    }

    // Get order details
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId, Authentication authentication) {
        if (isAdmin(authentication)) {
            // Admin can see any order
            return ResponseEntity.ok(adminService.getOrderById(orderId));
        } else {
            // User can only see their own orders
            String username = authentication.getName();
            try {
                OrderDTO order = orderService.getOrderDetails(orderId, username);
                return ResponseEntity.ok(order);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
        }
    }

    // Cancel an order
    @PatchMapping("/orders/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, Authentication authentication) {
        try {
            if (isAdmin(authentication)) {
                // Admin can cancel any order
                boolean isCanceled = orderService.cancelOrder(orderId);
                if (isCanceled) {
                    return ResponseEntity.ok("Order canceled successfully.");
                } else {
                    return ResponseEntity.badRequest().body("Order cannot be canceled.");
                }
            } else {
                // User can only cancel their own orders
                String username = authentication.getName();
                // Verify order belongs to user
                OrderDTO order = orderService.getOrderDetails(orderId, username);

                boolean isCanceled = orderService.cancelOrder(orderId);
                if (isCanceled) {
                    return ResponseEntity.ok("Order canceled successfully.");
                } else {
                    return ResponseEntity.badRequest().body("Order cannot be canceled.");
                }
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    // Complete order - admin only
    @PatchMapping("/orders/{orderId}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable Long orderId, Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        try {
            orderService.completeOrder(orderId);
            return ResponseEntity.ok("Order completed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}