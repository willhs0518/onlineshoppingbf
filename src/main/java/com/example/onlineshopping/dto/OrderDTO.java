package com.example.onlineshopping.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private Long orderId;
    private LocalDateTime datePlaced;
    private String orderStatus;
    private String username;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)  //  Hides 'items' if empty or null
    private List<OrderItemDTO> items;

    public OrderDTO(Long orderId, LocalDateTime datePlaced, String orderStatus) {
        this.orderId = orderId;
        this.datePlaced = datePlaced;
        this.orderStatus = orderStatus;
        this.items = null;
    }

    public OrderDTO(Long orderId, LocalDateTime datePlaced, String orderStatus, List<OrderItemDTO> items) {
        this.orderId = orderId;
        this.datePlaced = datePlaced;
        this.orderStatus = orderStatus;
        this.items = items;
    }

    public OrderDTO(Long orderId, LocalDateTime datePlaced, String orderStatus, String username) {
        this.orderId = orderId;
        this.datePlaced = datePlaced;
        this.orderStatus = orderStatus;
        this.username = username;
    }


    public Long getOrderId() { return orderId; }
    public LocalDateTime getDatePlaced() { return datePlaced; }
    public String getOrderStatus() { return orderStatus; }
    public List<OrderItemDTO> getItems() { return items; }
}
