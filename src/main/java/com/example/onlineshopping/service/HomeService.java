package com.example.onlineshopping.service;

import com.example.onlineshopping.dao.OrderDao;
import com.example.onlineshopping.dao.ProductDao;
import com.example.onlineshopping.domain.hibernate.ProductHibernate;
import com.example.onlineshopping.dto.OrderDTO;
import com.example.onlineshopping.dto.ProductDTO;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HomeService {
    @Autowired
    private ProductDao productDao;

    @Autowired
    private OrderDao orderDao;

    // Fetch all available (in-stock) products.
    public List<ProductDTO> getAvailableProducts() {
        List<ProductHibernate> products = productDao.findAvailableProducts();

        // Convert ProductHibernate to ProductDTO to include only name, description, and retailPrice
        return products.stream()
                .map(product -> new ProductDTO(
                        product.getProductId(),
                        product.getName(),
                        product.getDescription(),
                        product.getRetailPrice()
                ))
                .collect(Collectors.toList());
    }


    // Get product details by ID.
    public ProductDTO getProductDetails(Long productId) {
        ProductHibernate product = productDao.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }
        return new ProductDTO(product.getProductId(),product.getName(), product.getDescription(), product.getRetailPrice());
    }

    @Transactional
    public List<OrderDTO> getRecentOrders(Long userId) {
        Session session = orderDao.openSession();
        try {
            String hql = "SELECT o.orderId, o.datePlaced, o.orderStatus " +
                    "FROM OrderHibernate o " +
                    "WHERE o.userHibernate.userId = :userId " +
                    "ORDER BY o.datePlaced DESC";

            Query<Object[]> query = session.createQuery(hql, Object[].class)
                    .setParameter("userId", userId)
                    .setMaxResults(5);  // Get the latest 5 orders

            return query.list().stream()
                    .map(row -> new OrderDTO(
                            (Long) row[0],  // orderId
                            (LocalDateTime) row[1], // datePlaced
                            (String) row[2]  // orderStatus
                    ))
                    .collect(Collectors.toList());

        } finally {
            session.close();
        }
    }
}

