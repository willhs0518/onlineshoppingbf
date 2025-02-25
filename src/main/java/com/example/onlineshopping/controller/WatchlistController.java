package com.example.onlineshopping.controller;

import com.example.onlineshopping.domain.hibernate.WatchlistHibernate;
import com.example.onlineshopping.dto.WatchlistDTO;
import com.example.onlineshopping.service.WatchlistService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    @Autowired
    private WatchlistService watchlistService;

    @PostMapping
    public ResponseEntity<?> addToWatchlist(@RequestBody WatchlistHibernate watchlist
    , @AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedUsername = userDetails.getUsername();
        watchlistService.addToWatchlist(authenticatedUsername, watchlist);
        return ResponseEntity.ok("Product added to watchlist");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromWatchlist(@PathVariable Long productId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedUsername = userDetails.getUsername(); // Get logged-in user
        watchlistService.removeFromWatchlist(authenticatedUsername, productId);
        return ResponseEntity.ok("Product removed from watchlist");
    }

    @GetMapping
    public ResponseEntity<?> getWatchlist(@AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedUsername = userDetails.getUsername(); // Get logged-in user
        List<WatchlistDTO> products = watchlistService.getWatchlistProducts(authenticatedUsername);
        return ResponseEntity.ok(products);
    }
}
