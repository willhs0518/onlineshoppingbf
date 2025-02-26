package com.example.onlineshopping.dao;

import com.example.onlineshopping.domain.hibernate.UserHibernate;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.*;

@Repository
// @Transactional
public class UserDao extends AbstractHibernateDao<UserHibernate> {
    public UserDao() {
        super(UserHibernate.class);
    }

    public UserHibernate findByUsername(String username) {
        Session session = openSession();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<UserHibernate> criteria = builder.createQuery(UserHibernate.class);
            Root<UserHibernate> root = criteria.from(UserHibernate.class);
            criteria.where(builder.equal(root.get("username"), username));
            return session.createQuery(criteria).uniqueResult();
        } finally {
            session.close();
        }

    }

    public UserHibernate findByEmail(String email) {
        Session session = openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<UserHibernate> cq = cb.createQuery(UserHibernate.class);
            Root<UserHibernate> root = cq.from(UserHibernate.class);

            // WHERE email = :email
            cq.select(root).where(cb.equal(root.get("email"), email));

            return session.createQuery(cq).uniqueResult();
        } finally {
            session.close();
        }
    }

    public UserHibernate findById(Long id) {
        Session session = openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<UserHibernate> cq = cb.createQuery(UserHibernate.class);
            Root<UserHibernate> root = cq.from(UserHibernate.class);

            cq.select(root).where(cb.equal(root.get("userId"), id));

            return session.createQuery(cq).uniqueResult();
        } finally {
            session.close();
        }
    }
}
