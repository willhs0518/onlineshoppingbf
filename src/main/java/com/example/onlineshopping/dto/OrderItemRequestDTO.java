package com.example.onlineshopping.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDTO {
    private Long productId;
    private int quantity;

    public OrderItemRequestDTO(Long productId, int quantity) {}
    public OrderItemRequestDTO() {}

    public Long getProductId() { return productId; }
    public int getQuantity() { return quantity; }
}

