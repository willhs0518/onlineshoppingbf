package com.example.onlineshopping.dto;

import com.example.onlineshopping.domain.hibernate.UserHibernate;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {
    private String email;
    private List<OrderItemRequestDTO> order;

    public OrderRequestDTO() {}
    public OrderRequestDTO(UserHibernate user, List<OrderItemRequestDTO> items) {}

    public List<OrderItemRequestDTO> getOrder() {return order;}
    public String getEmail() {return email;}
}

