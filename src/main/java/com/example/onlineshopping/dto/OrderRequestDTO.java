package com.example.onlineshopping.dto;

import com.example.onlineshopping.domain.hibernate.UserHibernate;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {
    //private UserHibernate user;
    private String email;
    private List<OrderItemRequestDTO> items;

    public OrderRequestDTO() {}
    public OrderRequestDTO(UserHibernate user, List<OrderItemRequestDTO> items) {}

   // public UserHibernate getUser() {return user;}
    public List<OrderItemRequestDTO> getItems() {return items;}
    public String getEmail() {return email;}
}

