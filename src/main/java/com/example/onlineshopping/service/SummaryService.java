package com.example.onlineshopping.service;

import com.example.onlineshopping.dao.OrderDao;
import com.example.onlineshopping.dao.ProductDao;
import com.example.onlineshopping.domain.hibernate.OrderItemHibernate;
import com.example.onlineshopping.domain.hibernate.ProductHibernate;
import com.example.onlineshopping.dto.OrderDTO;
import com.example.onlineshopping.dto.ProductMostDTO;
import com.example.onlineshopping.dto.ProductProfitDTO;
import com.example.onlineshopping.exception.DuplicateProductException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SummaryService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private OrderDao orderDao;

    @Transactional
    public ProductProfitDTO getMostProfitableProduct() {
        Session session = orderDao.openSession(); // Consider using Spring-managed session instead
        try {
            String hql = "SELECT oi.productHibernate, SUM((oi.purchasedPrice - oi.wholesalePrice) * oi.quantity) AS profit " +
                    "FROM OrderItemHibernate oi " +
                    "JOIN oi.orderHibernate o " +
                    "WHERE o.orderStatus = 'Completed' " +
                    "GROUP BY oi.productHibernate " +
                    "ORDER BY profit DESC";

            List<Object[]> result = session.createQuery(hql).setMaxResults(3).list();
            if (result.isEmpty()) return null;

            ProductHibernate product = (ProductHibernate) result.get(0)[0];
            double profit = ((Number) result.get(0)[1]).doubleValue();  // Fix casting issue


            return new ProductProfitDTO(
                    product.getProductId(),
                    product.getName(),
                    product.getDescription(),
                    product.getQuantity(),
                    product.getRetailPrice(),
                    product.getWholesalePrice(),
                    profit
            );
        } finally {
            session.close();
        }
    }

    @Transactional
    public List<ProductMostDTO> getTopSellingProducts() {
        Session session = orderDao.openSession();
        try {
            String hql = "SELECT oi.productHibernate, SUM(oi.quantity) AS totalSold " +
                    "FROM OrderItemHibernate oi " +
                    "JOIN oi.orderHibernate o " +
                    "WHERE o.orderStatus = 'Completed' " +
                    "GROUP BY oi.productHibernate " +
                    "ORDER BY totalSold DESC";

            List<Object[]> results = session.createQuery(hql).setMaxResults(3).list();

            return results.stream().map(r -> {
                ProductHibernate product = (ProductHibernate) r[0];
                long totalSold = (long) r[1];

                return new ProductMostDTO(
                        product.getProductId(),
                        product.getName(),
                        product.getDescription(),
                        product.getRetailPrice(),
                        product.getWholesalePrice(),
                        totalSold   // Total number sold
                );
            }).collect(Collectors.toList());
        } finally {
            session.close();
        }
    }

    @Transactional
    public Long getTotalItemsSold() {
        Session session = orderDao.openSession();
        try {
            String hql = "SELECT SUM(oi.quantity) " +
                    "FROM OrderItemHibernate oi " +
                    "JOIN oi.orderHibernate o " +
                    "WHERE o.orderStatus = 'Completed'";

            return (Long) session.createQuery(hql).uniqueResult();
        } finally {
            session.close();
        }
    }

}
