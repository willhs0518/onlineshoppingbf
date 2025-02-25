package com.example.onlineshopping.controller;

import com.example.onlineshopping.dto.ProductMostDTO;
import com.example.onlineshopping.dto.ProductProfitDTO;
import com.example.onlineshopping.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/summary")
@CrossOrigin
public class SummaryController {

    @Autowired
    private SummaryService summaryService;

    @GetMapping(value = "/profitable-product")
    public ResponseEntity<ProductProfitDTO> getMostProfitableProduct() {
        ProductProfitDTO product = summaryService.getMostProfitableProduct();
        if (product == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping(value = "/top-selling-products")
    public ResponseEntity<List<ProductMostDTO>> getTopSellingProducts() {
        List<ProductMostDTO> products = summaryService.getTopSellingProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/total-items-sold")
    public ResponseEntity<Long> getTotalItemsSold() {
        return ResponseEntity.ok(summaryService.getTotalItemsSold());
    }
}

