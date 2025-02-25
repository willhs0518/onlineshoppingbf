package com.example.onlineshopping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductMostDTO {
    private Long productId;
    private String name;
    private String description;
    private double retailPrice;
    private double wholesalePrice;
    private long totalSold;

    public ProductMostDTO() {}

    public ProductMostDTO(Long productId, String name, String description, double retailPrice, double wholesalePrice, long totalSold) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.retailPrice = retailPrice;
        this.wholesalePrice = wholesalePrice;
        this.totalSold = totalSold;
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return name; }
    public String getDescription() { return description; }
    public double getRetailPrice() { return retailPrice; }
    public double getWholesalePrice() { return wholesalePrice; }
    public long getTotalSold() { return totalSold; }
}
