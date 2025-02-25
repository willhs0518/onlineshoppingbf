package com.example.onlineshopping.domain.hibernate;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItemHibernate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "purchased_price", nullable = false)
    private double purchasedPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "wholesale_price", nullable = false)
    private double wholesalePrice;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private OrderHibernate orderHibernate;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductHibernate productHibernate;

    public OrderItemHibernate() {
    }

    public OrderItemHibernate(double purchasedPrice, int quantity, double wholesalePrice,
                              OrderHibernate orderHibernate, ProductHibernate productHibernate) {
        this.purchasedPrice = purchasedPrice;
        this.quantity = quantity;
        this.wholesalePrice = wholesalePrice;
        this.orderHibernate = orderHibernate;
        this.productHibernate = productHibernate;
    }

    public double getPurchasedPrice() { return purchasedPrice; }
    public int getQuantity() { return quantity; }
    public double getWholesalePrice() { return wholesalePrice; }
    public OrderHibernate getOrderHibernate() { return orderHibernate; }
    public ProductHibernate getProductHibernate() { return productHibernate; }

    public void setPurchasedPrice(double purchasedPrice) { this.purchasedPrice = purchasedPrice; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setWholesalePrice(double wholesalePrice) { this.wholesalePrice = wholesalePrice; }
    public void setOrderHibernate(OrderHibernate orderHibernate) { this.orderHibernate = orderHibernate; }
    public void setProductHibernate(ProductHibernate productHibernate) { this.productHibernate = productHibernate; }

}

