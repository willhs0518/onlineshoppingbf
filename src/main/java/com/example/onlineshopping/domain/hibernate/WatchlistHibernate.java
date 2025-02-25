package com.example.onlineshopping.domain.hibernate;

import javax.persistence.*;
import com.example.onlineshopping.domain.WatchlistId;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "watchlist")
@IdClass(WatchlistId.class)
public class WatchlistHibernate {
//    @Id
//    @Column(name = "user_id") // Primary Key part 1
//    private Long userId;
//
//    @Id
//    @Column(name = "product_id") // Primary Key part 2
//    private Long productId;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private UserHibernate userHibernate;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, insertable = false, updatable = false)
    private ProductHibernate productHibernate;

    public WatchlistHibernate() {};

    public UserHibernate getUserHibernate() { return userHibernate; }
    public void setUserHibernate(UserHibernate userHibernate) { this.userHibernate = userHibernate; }
    public ProductHibernate getProductHibernate() { return productHibernate; }
    public void setProductHibernate(ProductHibernate productHibernate) { this.productHibernate = productHibernate; }
}
