package com.example.onlineshopping.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@Repository
// @Transactional
public abstract class AbstractHibernateDao<T> {
    @Autowired
    protected SessionFactory sessionFactory;
    protected Class<T> entityClass;

    public AbstractHibernateDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }

    public void save(T entity) {
        openSession().save(entity);
    }

    public void update(T entity) {
        openSession().update(entity);
    }

    public T findById(Serializable id) {
        return openSession().get(entityClass, id);
    }

    public void delete(T entity) {
        openSession().delete(entity);
    }

    public List<T> getAll() {
        Session session = openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(entityClass);
        criteria.from(entityClass);
        return session.createQuery(criteria).getResultList();
    }
}