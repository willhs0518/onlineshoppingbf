package com.example.onlineshopping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductProfitDTO {
    private Long productId;
    private String name;
    private String description;
    private int quantity;
    private double retailPrice;
    private double wholesalePrice;
    private double profit;
    private long totalSold;

    public ProductProfitDTO() {}

    public ProductProfitDTO(Long productId, String name, String description, int quantity, double retailPrice, double wholesalePrice, double profit) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.retailPrice = retailPrice;
        this.wholesalePrice = wholesalePrice;
        this.profit = profit;
    }

    public ProductProfitDTO(Long productId, String name, String description, int quantity, double retailPrice, double wholesalePrice, double profit, long totalSold) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.retailPrice = retailPrice;
        this.wholesalePrice = wholesalePrice;
        this.profit = profit;
        this.totalSold = totalSold;
    }


    public Long getProductId() { return productId; }
    public String getProductName() { return name; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public double getRetailPrice() { return retailPrice; }
    public double getWholesalePrice() { return wholesalePrice; }
    public double getProfit() { return profit;}
   // public long getTotalSold() { return totalSold; }
}
