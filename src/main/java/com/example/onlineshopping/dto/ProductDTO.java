package com.example.onlineshopping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private int quantity;
    private double wholesalePrice;
    private double retailPrice;

    // No-args constructor
    public ProductDTO() {
    }

    // All-args constructor
    public ProductDTO(Long ProductId, String name, String description, double retailPrice) {
        this.productId = ProductId;
        this.name = name;
        this.description = description;
        this.retailPrice = retailPrice;
    }

    public ProductDTO(Long productId, String name, String description, int quantity, double wholesalePrice, double retailPrice) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.wholesalePrice = wholesalePrice;
        this.retailPrice = retailPrice;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }
}


