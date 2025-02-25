package com.example.onlineshopping.service;

import com.example.onlineshopping.dao.OrderDao;
import com.example.onlineshopping.dao.ProductDao;
import com.example.onlineshopping.domain.hibernate.OrderHibernate;
import com.example.onlineshopping.domain.hibernate.ProductHibernate;
import com.example.onlineshopping.dto.OrderDTO;
import com.example.onlineshopping.exception.DuplicateProductException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    // Get all orders
    @Transactional
    public List<OrderDTO> getAllOrders() {
        Session session = orderDao.openSession();
        List<Object[]> results = session.createQuery(
                "SELECT o.orderId, o.datePlaced, o.orderStatus, u.username " +
                        "FROM OrderHibernate o " +
                        "JOIN o.userHibernate u",  // Fetch only username
                Object[].class
        ).list();

        return results.stream().map(row ->
                new OrderDTO(
                        (Long) row[0],
                        (LocalDateTime) row[1],
                        (String) row[2],
                        (String) row[3]  // Include only username
                )
        ).collect(Collectors.toList());
    }

    // Get order by ID
    @Transactional
    public OrderDTO getOrderById(Long orderId) {
        OrderHibernate order = orderDao.findById(orderId);
        return new OrderDTO(
                order.getOrderId(),
                order.getDatePlaced(),
                order.getOrderStatus(),
                order.getUserHibernate().getUsername() // Include only username
        );
    }

    // Get all product listings
    @Transactional
    public List<ProductHibernate> getAllProducts() {
        return productDao.findAll();
    }

    // Get product by ID
    @Transactional
    public ProductHibernate getProductById(Long productId) {
        return productDao.findById(productId);
    }

    // Update product details
    @Transactional
    public ProductHibernate updateProduct(ProductHibernate product) {
        Session session = productDao.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction(); // Start Transaction

            ProductHibernate existingProduct = session.get(ProductHibernate.class, product.getProductId());
            if (existingProduct == null) {
                throw new IllegalArgumentException("Product not found");
            }

            // Update only non-null fields
            if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());
            if (product.getWholesalePrice() != 0) existingProduct.setWholesalePrice(product.getWholesalePrice());
            if (product.getRetailPrice() != 0) existingProduct.setRetailPrice(product.getRetailPrice());
            if (product.getQuantity() != 0) existingProduct.setQuantity(product.getQuantity());

            session.update(existingProduct);
            session.flush(); // Ensure update is applied
            transaction.commit();

            return existingProduct;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Transactional
    public ProductHibernate addProduct(ProductHibernate product) {
        ProductHibernate existingProduct = productDao.findByName(product.getName());

        if (existingProduct != null) {
            throw new DuplicateProductException("Product with name '" + product.getName() + "' already exists.");
        }

        productDao.addproduct(product);
        return product;
    }
}
