package com.example.onlineshopping.domain.hibernate;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`order`")
public class OrderHibernate{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "date_placed", nullable = false)
    private LocalDateTime datePlaced;

    @Column(name = "order_status", nullable = false)
    private String orderStatus;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserHibernate userHibernate;

    @OneToMany(mappedBy = "orderHibernate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItemHibernate> orderItems = new ArrayList<>();

    public String getOrderStatus() { return orderStatus; }
    public UserHibernate getUserHibernate() { return userHibernate; }
    public List<OrderItemHibernate> getOrderItems() { return orderItems; }
    public Long getOrderId() { return orderId; }
    public LocalDateTime getDatePlaced() { return datePlaced; }

    public void setDatePlaced(LocalDateTime datePlaced) { this.datePlaced = datePlaced; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public void setUserHibernate(UserHibernate userHibernate) { this.userHibernate = userHibernate; }
    public void setOrderItems(List<OrderItemHibernate> orderItems) { this.orderItems = orderItems; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

}

