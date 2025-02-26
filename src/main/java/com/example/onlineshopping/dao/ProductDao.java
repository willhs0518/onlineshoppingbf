package com.example.onlineshopping.dao;

import com.example.onlineshopping.domain.hibernate.ProductHibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
// @Transactional
public class ProductDao extends AbstractHibernateDao<ProductHibernate> {

    public ProductDao() {
        super(ProductHibernate.class);
    }

    //Find all available (in-stock) products.
    public List<ProductHibernate> findAvailableProducts() {
        Session session = openSession();
        String hql = "FROM ProductHibernate WHERE quantity > 0";
        Query<ProductHibernate> query = session.createQuery(hql, ProductHibernate.class);
        return query.getResultList();
    }

    // Find product by ID.
    public ProductHibernate findById(Long productId) {
        Session session = openSession();
        try {
            String hql = "FROM ProductHibernate WHERE productId = :productId";
            return (ProductHibernate) session.createQuery(hql)
                    .setParameter("productId", productId)
                    .uniqueResult();
        } finally {
            session.close();
        }
    }

    // Find product by Name.
    public ProductHibernate findByName(String name) {
        Session session = openSession();
        try {
            String hql = "FROM ProductHibernate WHERE name = :name";
            return session.createQuery(hql, ProductHibernate.class)
                    .setParameter("name", name)
                    .uniqueResult();
        } finally {
            session.close();
        }
    }

    // Update stock quantity when user purchase.
    public void update(ProductHibernate product) {
        Session session = openSession();
        Transaction tx = session.beginTransaction();
        try {
            String hql = "UPDATE ProductHibernate SET " +
                    "quantity = :quantity " +
                    "WHERE productId = :productId";

            session.createQuery(hql)
                    .setParameter("quantity", product.getQuantity())
                    .setParameter("productId", product.getProductId())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    // Get all products
    public List<ProductHibernate> findAll() {
        Session session = openSession();
        String hql = "FROM ProductHibernate ORDER BY productId ASC";
        Query<ProductHibernate> query = session.createQuery(hql, ProductHibernate.class);
        List<ProductHibernate> products = query.list();
        session.close();
        return products;
    }

    public void addproduct(ProductHibernate product) {
        Session session = openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(product);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

}

