package com.example.onlineshopping.controller;

import com.example.onlineshopping.domain.hibernate.ProductHibernate;
import com.example.onlineshopping.dto.OrderDTO;
import com.example.onlineshopping.dto.ProductDTO;
import com.example.onlineshopping.service.HomeService;
import com.example.onlineshopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@CrossOrigin
public class HomeController {

    @Autowired
    private HomeService homeService;


     // Get all available (in-stock) products for the home page.
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAvailableProducts() {
        List<ProductDTO> products = homeService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Get product details by ID.
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDTO> getProductDetails(@PathVariable Long productId) {
        ProductDTO product = homeService.getProductDetails(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/recent/{userId}")
    public ResponseEntity<?> getRecentOrders(@PathVariable Long userId) {
        List<OrderDTO> orders = homeService.getRecentOrders(userId);
        return ResponseEntity.ok(orders);
    }
}

