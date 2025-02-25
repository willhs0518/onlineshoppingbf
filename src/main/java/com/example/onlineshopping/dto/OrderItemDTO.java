package com.example.onlineshopping.dto;

import java.time.LocalDateTime;

public class OrderItemDTO {
    private Long productId;
    private String name;
    private int quantity;
    private double price;
    private LocalDateTime datePlaced;

    public OrderItemDTO(Long productId, String name, int quantity, double price, LocalDateTime datePlaced) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.datePlaced = datePlaced;
    }

    public OrderItemDTO(Long productId, String name, int quantity, double price) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getProductId() { return productId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    // public double getPrice() { return price; }
    //public LocalDateTime getDatePlaced() { return datePlaced; }
}

