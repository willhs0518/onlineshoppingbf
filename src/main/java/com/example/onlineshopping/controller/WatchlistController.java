package com.example.onlineshopping.controller;

import com.example.onlineshopping.domain.hibernate.ProductHibernate;
import com.example.onlineshopping.domain.hibernate.WatchlistHibernate;
import com.example.onlineshopping.dto.WatchlistDTO;
import com.example.onlineshopping.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class WatchlistController {
    @Autowired
    private WatchlistService watchlistService;

    // Add product to watchlist
    @PostMapping("/watchlist/product/{productId}")
    public ResponseEntity<?> addToWatchlist(@PathVariable Long productId, Authentication authentication) {
        String username = authentication.getName();

        // Create the WatchlistHibernate object with just the productId
        WatchlistHibernate watchlist = new WatchlistHibernate();
        ProductHibernate product = new ProductHibernate();
        product.setProductId(productId);
        watchlist.setProductHibernate(product);

        try {
            watchlistService.addToWatchlist(username, watchlist);
            return ResponseEntity.ok("Product added to watchlist");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Remove product from watchlist
    @DeleteMapping("/watchlist/product/{productId}")
    public ResponseEntity<?> removeFromWatchlist(@PathVariable Long productId, Authentication authentication) {
        String username = authentication.getName();
        try {
            watchlistService.removeFromWatchlist(username, productId);
            return ResponseEntity.ok("Product removed from watchlist");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all watchlist products
    @GetMapping("/watchlist/products/all")
    public ResponseEntity<?> getWatchlist(Authentication authentication) {
        String username = authentication.getName();
        try {
            List<WatchlistDTO> products = watchlistService.getWatchlistProducts(username);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}