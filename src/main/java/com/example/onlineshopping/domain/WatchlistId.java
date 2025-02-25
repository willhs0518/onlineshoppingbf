package com.example.onlineshopping.domain;

import java.io.Serializable;

// composite key class
public class WatchlistId implements Serializable {
    private Long userHibernate;   //  Must match WatchlistHibernate field name
    private Long productHibernate; //  Must match WatchlistHibernate field name

    // Constructors
    public WatchlistId() {}

    public WatchlistId(Long userHibernate, Long productHibernate) {
        this.userHibernate = userHibernate;
        this.productHibernate = productHibernate;
    }

    // Getters and Setters
    public Long getUserHibernate() { return userHibernate; }
    public void setUserHibernate(Long userHibernate) { this.userHibernate = userHibernate; }
    public Long getProductHibernate() { return productHibernate; }
    public void setProductHibernate(Long productHibernate) { this.productHibernate = productHibernate; }

}


