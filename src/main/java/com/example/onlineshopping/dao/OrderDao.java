package com.example.onlineshopping.dao;

import com.example.onlineshopping.domain.hibernate.OrderHibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
// @Transactional
public class OrderDao extends AbstractHibernateDao<OrderHibernate> {
    public OrderDao() {
        super(OrderHibernate.class);
    }

    // Get all orders
    public List<OrderHibernate> findAll() {
        Session session = openSession();
        String hql = "FROM OrderHibernate ORDER BY datePlaced DESC";
        Query<OrderHibernate> query = session.createQuery(hql, OrderHibernate.class);
        List<OrderHibernate> orders = query.list();
        session.close();
        return orders;
    }

    // Get order by ID (pure hibernate api)
    public OrderHibernate findById(Long orderId) {
        Session session = openSession();
        try {
            OrderHibernate order = session.get(OrderHibernate.class, orderId);
            return order;
        } finally {
            session.close();
        }
    }
}
