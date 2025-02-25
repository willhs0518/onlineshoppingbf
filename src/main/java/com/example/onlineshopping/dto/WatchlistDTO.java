package com.example.onlineshopping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WatchlistDTO {
    private Long productId;
    private String name;
    private String description;
    private double retailPrice;

    public WatchlistDTO() {}

    public WatchlistDTO(Long productId, String name, String description, double retailPrice) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.retailPrice = retailPrice;
    }

    public Long getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getRetailPrice() { return retailPrice; }

    // Setters
    public void setProductId(Long productId) { this.productId = productId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setRetailPrice(double retailPrice) { this.retailPrice = retailPrice; }

}


