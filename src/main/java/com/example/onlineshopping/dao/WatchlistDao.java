package com.example.onlineshopping.dao;

import com.example.onlineshopping.domain.WatchlistId;
import com.example.onlineshopping.domain.hibernate.WatchlistHibernate;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public class WatchlistDao extends AbstractHibernateDao<WatchlistHibernate> {
    public WatchlistDao() {
        super(WatchlistHibernate.class);
    }

    public WatchlistHibernate findById(WatchlistId id) {
        Session session = openSession();
        try {
            String hql = "FROM WatchlistHibernate w " +
                    "WHERE w.userHibernate.userId = :userHibernate " +
                    "AND w.productHibernate.productId = :productHibernate";

            return (WatchlistHibernate) session.createQuery(hql)
                    .setParameter("userHibernate", id.getUserHibernate())
                    .setParameter("productHibernate", id.getProductHibernate())
                    .uniqueResult();
        } finally {
            session.close();
        }
    }
}
