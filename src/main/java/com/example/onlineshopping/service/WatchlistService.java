package com.example.onlineshopping.service;

import com.example.onlineshopping.dao.ProductDao;
import com.example.onlineshopping.dao.UserDao;
import com.example.onlineshopping.dao.WatchlistDao;
import com.example.onlineshopping.domain.WatchlistId;
import com.example.onlineshopping.domain.hibernate.ProductHibernate;
import com.example.onlineshopping.domain.hibernate.UserHibernate;
import com.example.onlineshopping.domain.hibernate.WatchlistHibernate;
import com.example.onlineshopping.dto.WatchlistDTO;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class WatchlistService {
    @Autowired
    private WatchlistDao watchlistDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private UserDao userDao;

    public void addToWatchlist(String authenticatedUsername, WatchlistHibernate watchlist) {
        Session session = watchlistDao.openSession();
        Transaction tx = session.beginTransaction();
        try {
            // Load the full entities
            UserHibernate user = userDao.findByUsername(authenticatedUsername);

            // UserHibernate user = userDao.findById(watchlist.getUserHibernate().getUserId());
            ProductHibernate product = productDao.findById(watchlist.getProductHibernate().getProductId());

            if (user == null || product == null) {
                throw new IllegalArgumentException("User or Product not found");
            }

            if (product.getQuantity() < 1) {
                throw new IllegalArgumentException("Product is out of stock");
            }

            String hql = "SELECT COUNT(w) FROM WatchlistHibernate w WHERE w.userHibernate.userId = :userId AND w.productHibernate.productId = :productId";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("userId", user.getUserId())
                    .setParameter("productId", product.getProductId())
                    .uniqueResult();

            if (count > 0) {
                throw new IllegalArgumentException("Product is already in the watchlist.");
            }

            // Create new watchlist entry with loaded entities
            WatchlistHibernate newWatchlist = new WatchlistHibernate();
            newWatchlist.setUserHibernate(user);
            newWatchlist.setProductHibernate(product);

            session.save(newWatchlist);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    @Transactional
    public void removeFromWatchlist(String authenticatedUsername, Long productId) {
        Session session = watchlistDao.openSession();
        try {
            UserHibernate user = userDao.findByUsername(authenticatedUsername);
            if (user == null) {
                throw new IllegalArgumentException("User not found: " + authenticatedUsername);
            }

            WatchlistHibernate watchlist = watchlistDao.findById(new WatchlistId(user.getUserId(), productId));
            if (watchlist == null) {
                throw new IllegalArgumentException("Product not in watchlist");
            }

            session.beginTransaction();
            session.delete(watchlist);
            session.getTransaction().commit();

            System.out.println("Removed successfully: " + authenticatedUsername + " -> " + productId);
        } finally {
            session.close();
        }
    }

    @Transactional
    public List<WatchlistDTO> getWatchlistProducts(String authenticatedUsername) {
        Session session = watchlistDao.openSession();
        try {
            UserHibernate user = userDao.findByUsername(authenticatedUsername);
            if (user == null) {
                throw new IllegalArgumentException("User not found: " + authenticatedUsername);
            }

            String hql = "SELECT new com.example.onlineshopping.dto.WatchlistDTO(p.productId, p.name, p.description, p.retailPrice) " +
                    "FROM WatchlistHibernate w JOIN w.productHibernate p " +
                    "WHERE w.userHibernate.userId = :userId";

            return session.createQuery(hql, WatchlistDTO.class)
                    .setParameter("userId", user.getUserId())
                    .list();
        } finally {
            session.close();
        }
    }

//
//    public void removeFromWatchlist(Long userHibernate, Long productHibernate) {
//        WatchlistHibernate watchlist = watchlistDao.findById(new WatchlistId(userHibernate, productHibernate));
//        if (watchlist == null) {
//            throw new IllegalArgumentException("Product not in watchlist");
//        }
//        watchlistDao.delete(watchlist);
//
//        // Ensure deletion takes effect
//        Session session = watchlistDao.openSession();
//        session.beginTransaction();
//        session.delete(watchlist);
//        session.getTransaction().commit();
//        session.close();
//
//        System.out.println("Removed successfully: " + userHibernate + " -> " + productHibernate);
//    }
//
//    public List<WatchlistDTO> getWatchlistProducts(Long userHibernate) {
//        Session session = watchlistDao.openSession();
//        try {
//            String hql = "SELECT new com.example.onlineshopping.dto.WatchlistDTO(p.productId, p.name, p.description, p.retailPrice) " +
//                    "FROM WatchlistHibernate w JOIN w.productHibernate p " +
//                    "WHERE w.userHibernate.userId = :userId";
//
//            return session.createQuery(hql, WatchlistDTO.class)
//                    .setParameter("userId", userHibernate)
//                    .list();
//        } finally {
//            session.close();
//        }
//    }
}
