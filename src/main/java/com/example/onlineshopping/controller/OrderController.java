package com.example.onlineshopping.controller;

import com.example.onlineshopping.dto.OrderDTO;
import com.example.onlineshopping.dto.OrderItemDTO;
import com.example.onlineshopping.dto.OrderRequestDTO;
import com.example.onlineshopping.exception.OrderCancellationException;
import com.example.onlineshopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Place a new order

    @PostMapping("/purchase")
    public ResponseEntity<?> placeOrder(
            @RequestBody OrderRequestDTO orderRequest,
            @AuthenticationPrincipal UserDetails userDetails // Extract username
    ) {
        String authenticatedUsername = userDetails.getUsername();  // Get username instead
        Long orderId = orderService.placeOrder(authenticatedUsername, orderRequest.getItems());
        return ResponseEntity.ok("Order created in Processing status. Order ID: " + orderId);
    }

    //Cancel an order
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        boolean isCanceled = orderService.cancelOrder(orderId);
        if (!isCanceled) {
            throw new OrderCancellationException("Order cannot be canceled.");
        }
        return ResponseEntity.ok("Order canceled successfully.");
    }

//    @GetMapping("/{orderId}")
//    public ResponseEntity<?> getOrderStatus(@PathVariable Long orderId) {
//        String status = orderService.getOrderStatus(orderId);
//        return ResponseEntity.ok(status);
//    }
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderStatus(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        String status = orderService.getOrderStatus(orderId, username);
        return ResponseEntity.ok(status);
    }

    // Retrieve all orders for a specific user
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<?> getUserOrders(@PathVariable Long userId) {
//        List<OrderDTO> orders = orderService.getUserOrders(userId);
//        return ResponseEntity.ok(orders);
//    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<OrderDTO> orders = orderService.getUserOrders(username);
        return ResponseEntity.ok(orders);
    }

//    @GetMapping("/details/{orderId}")
//    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
//        OrderDTO order = orderService.getOrderDetails(orderId);
//        return ResponseEntity.ok(order);
//    }
    @GetMapping("/details/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedUsername = userDetails.getUsername(); // Extract logged-in username
        OrderDTO order = orderService.getOrderDetails(orderId, authenticatedUsername);
        return ResponseEntity.ok(order);
    }

    // Retrieve top 3 most frequently purchased items
//    @GetMapping("/top-frequent/{userId}")
//    public ResponseEntity<?> getTopFrequentItems(@PathVariable Long userId) {
//        List<OrderItemDTO> items = orderService.getTopFrequentItems(userId);
//        return ResponseEntity.ok(items);
//    }
//
//    // Retrieve top 3 most recently purchased items
//    @GetMapping("/top-recent/{userId}")
//    public ResponseEntity<?> getTopRecentItems(@PathVariable Long userId) {
//        List<OrderItemDTO> items = orderService.getTopRecentItems(userId);
//        return ResponseEntity.ok(items);
//    }
    //  Retrieve top 3 most frequently purchased items (Authenticated user only)
    @GetMapping("/top-frequent")
    public ResponseEntity<?> getTopFrequentItems(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<OrderItemDTO> items = orderService.getTopFrequentItems(username);
        return ResponseEntity.ok(items);
    }

    //  Retrieve top 3 most recently purchased items (Authenticated user only)
    @GetMapping("/top-recent")
    public ResponseEntity<?> getTopRecentItems(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<OrderItemDTO> items = orderService.getTopRecentItems(username);
        return ResponseEntity.ok(items);
    }

}

