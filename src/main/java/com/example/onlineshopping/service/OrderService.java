package com.example.onlineshopping.service;

import com.example.onlineshopping.dao.OrderDao;
import com.example.onlineshopping.dao.ProductDao;
import com.example.onlineshopping.dao.UserDao;
import com.example.onlineshopping.domain.hibernate.OrderHibernate;
import com.example.onlineshopping.domain.hibernate.OrderItemHibernate;
import com.example.onlineshopping.domain.hibernate.ProductHibernate;
import com.example.onlineshopping.domain.hibernate.UserHibernate;
import com.example.onlineshopping.dto.OrderDTO;
import com.example.onlineshopping.dto.OrderItemDTO;
import com.example.onlineshopping.dto.OrderItemRequestDTO;
import com.example.onlineshopping.dto.OrderRequestDTO;
import com.example.onlineshopping.exception.NotEnoughInventoryException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    /**
     * Places an order and ensures stock availability.
     *
     * @return order ID
     */
    @Transactional
    public Long placeOrder(String authenticatedUsername, List<OrderItemRequestDTO> items) {
        Session session = orderDao.openSession();
        Transaction tx = session.beginTransaction();

        try {
            // Find user using username
            UserHibernate user = userDao.findByUsername(authenticatedUsername);
            if (user == null) {
                throw new IllegalArgumentException("User not found: " + authenticatedUsername);
            }

            OrderHibernate order = new OrderHibernate();
            order.setDatePlaced(LocalDateTime.now());
            order.setOrderStatus("Processing");
            order.setUserHibernate(user);

            // Process order items and validate inventory
            List<OrderItemHibernate> orderItems = items.stream()
                    .map(item -> {
                        ProductHibernate product = session.get(ProductHibernate.class, item.getProductId());

                        if (product.getQuantity() < item.getQuantity()) {
                            throw new NotEnoughInventoryException(
                                    "Not enough inventory for product: " + product.getName()
                            );
                        }

                        product.setQuantity(product.getQuantity() - item.getQuantity());
                        session.update(product);

                        return new OrderItemHibernate(
                                product.getRetailPrice(),
                                item.getQuantity(),
                                product.getWholesalePrice(),
                                order,
                                product
                        );
                    })
                    .collect(Collectors.toList());

            order.setOrderItems(orderItems);
            session.save(order);
            tx.commit();

            return order.getOrderId();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Transactional
    public void completeOrder(Long orderId) {
        Session session = orderDao.openSession();
        Transaction tx = session.beginTransaction();

        try {
            OrderHibernate order = session.get(OrderHibernate.class, orderId);

            if (order == null) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }

            if (!"Processing".equals(order.getOrderStatus())) {
                throw new IllegalArgumentException("Only Processing orders can be completed");
            }

            order.setOrderStatus("Completed");
            session.update(order);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * Retrieves the status of an order.
     */
//    @Transactional
//    public String getOrderStatus(Long orderId) {
//        OrderHibernate order = orderDao.findById(orderId);
//        if (order == null) {
//            throw new IllegalArgumentException("Order not found: " + orderId);
//        }
//
//        return order.getOrderStatus();
//    }
    @Transactional
    public String getOrderStatus(Long orderId, String authenticatedUsername) {
        Session session = orderDao.openSession();
        try {
            // Retrieve order
            OrderHibernate order = session.get(OrderHibernate.class, orderId);
            if (order == null) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }

            //  Ensure only the owner can view the order status
            if (!order.getUserHibernate().getUsername().equals(authenticatedUsername)) {
                throw new IllegalArgumentException("You are not authorized to view this order status.");
            }

            return order.getOrderStatus();
        } finally {
            session.close();
        }
    }


    /**
     * Cancels an order and restores stock.
     */
    @Transactional
    public boolean cancelOrder(Long orderId) {
        Session session = orderDao.openSession();
        OrderHibernate order = session.get(OrderHibernate.class, orderId);

        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        if (!"Processing".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("Only 'Processing' orders can be canceled.");
        }

        // Begin transaction
        Transaction tx = session.beginTransaction();

        try {
            // Update order status
            order.setOrderStatus("Canceled");

            // Restore product quantities
            for (OrderItemHibernate item : order.getOrderItems()) {
                ProductHibernate product = item.getProductHibernate();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                session.update(product);
            }

            // Save changes
            session.update(order);
            session.flush();  // Force the changes to be written to the database
            tx.commit();      // Commit the transaction

            return true;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    // Retrieve all orders for a given user
    @Transactional
    public List<OrderDTO> getUserOrders(String authenticatedUsername) {
        Session session = orderDao.openSession();
        try {
            UserHibernate user = userDao.findByUsername(authenticatedUsername);
            if (user == null) {
                throw new IllegalArgumentException("User not found.");
            }

            String hql = "FROM OrderHibernate o WHERE o.userHibernate.userId = :userId ORDER BY o.datePlaced DESC";
            List<OrderHibernate> orders = session.createQuery(hql, OrderHibernate.class)
                    .setParameter("userId", user.getUserId())
                    .list();

            return orders.stream()
                    .map(order -> new OrderDTO(
                            order.getOrderId(),
                            order.getDatePlaced(),
                            order.getOrderStatus(),
                            order.getOrderItems().stream()
                                    .map(item -> new OrderItemDTO(
                                            item.getProductHibernate().getProductId(),
                                            item.getProductHibernate().getName(),
                                            item.getQuantity(),
                                            item.getPurchasedPrice()))
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());

        } finally {
            session.close();
        }
    }
//    @Transactional
//    public List<OrderDTO> getUserOrders(Long userId) {
//        Session session = orderDao.openSession();
//        try {
//            String hql = "FROM OrderHibernate o WHERE o.userHibernate.userId = :userId ORDER BY o.datePlaced DESC";
//            List<OrderHibernate> orders = session.createQuery(hql, OrderHibernate.class)
//                    .setParameter("userId", userId)
//                    .list();
//
//            return orders.stream()
//                    .map(order -> new OrderDTO(
//                            order.getOrderId(),
//                            order.getDatePlaced(),
//                            order.getOrderStatus(),
//                            order.getOrderItems().stream()
//                                    .map(item -> new OrderItemDTO(
//                                            item.getProductHibernate().getProductId(),
//                                            item.getProductHibernate().getName(),
//                                            item.getQuantity(),
//                                            item.getPurchasedPrice()))
//                                    .collect(Collectors.toList())
//                    ))
//                    .collect(Collectors.toList());
//
//        } finally {
//            session.close();
//        }
//    }

//    @Transactional
//    public List<OrderItemDTO> getTopFrequentItems(Long userId) {
//        Session session = orderDao.openSession();
//        try {
//            String hql = "SELECT oi.productHibernate.productId, oi.productHibernate.name, " +
//                    "SUM(oi.quantity) AS totalQuantity, " +
//                    "MAX(oi.purchasedPrice), " +
//                    "MAX(o.datePlaced) " +
//                    "FROM OrderItemHibernate oi " +
//                    "JOIN oi.orderHibernate o " +
//                    "WHERE o.userHibernate.userId = :userId AND o.orderStatus != 'Canceled' " +
//                    "GROUP BY oi.productHibernate.productId, oi.productHibernate.name " +  //  Only group by productId & name
//                    "ORDER BY totalQuantity DESC, oi.productHibernate.productId ASC";
//
//            Query<Object[]> query = session.createQuery(hql, Object[].class)
//                    .setParameter("userId", userId)
//                    .setMaxResults(3);
//
//            return query.list().stream()
//                    .map(row -> new OrderItemDTO(
//                            (Long) row[0],   // productId
//                            (String) row[1], // productName
//                            ((Number) row[2]).intValue(), // Convert SUM result safely
//                            ((Number) row[3]).doubleValue(),  //  PurchasedPrice is aggregated
//                            (LocalDateTime) row[4]  //  Get latest datePlaced
//                    ))
//                    .collect(Collectors.toList());
//
//        } finally {
//            session.close();
//        }
//    }
    @Transactional
    public List<OrderItemDTO> getTopFrequentItems(String authenticatedUsername) {
        Session session = orderDao.openSession();
        try {
            UserHibernate user = userDao.findByUsername(authenticatedUsername);
            if (user == null) {
                throw new IllegalArgumentException("User not found.");
            }
            String hql = "SELECT oi.productHibernate.productId, oi.productHibernate.name, " +
                    "SUM(oi.quantity) AS totalQuantity, " +
                    "MAX(oi.purchasedPrice), " +
                    "MAX(o.datePlaced) " +
                    "FROM OrderItemHibernate oi " +
                    "JOIN oi.orderHibernate o " +
                    "WHERE o.userHibernate.userId = :userId AND o.orderStatus != 'Canceled' " +
                    "GROUP BY oi.productHibernate.productId, oi.productHibernate.name " +
                    "ORDER BY totalQuantity DESC, oi.productHibernate.productId ASC";
            Query<Object[]> query = session.createQuery(hql, Object[].class)
                    .setParameter("userId", user.getUserId())
                    .setMaxResults(3);
            return query.list().stream()
                    .map(row -> new OrderItemDTO(
                            (Long) row[0],
                            (String) row[1],
                            ((Number) row[2]).intValue(),
                            ((Number) row[3]).doubleValue(),
                            (LocalDateTime) row[4]
                    ))
                    .collect(Collectors.toList());
        } finally {
            session.close();
        }
    }



    // Retrieve top 3 most recently purchased items (excluding canceled orders)
//    @Transactional
//    public List<OrderItemDTO> getTopRecentItems(Long userId) {
//        Session session = orderDao.openSession();
//        try {
//            String hql = "SELECT oi.productHibernate.productId, oi.productHibernate.name, oi.quantity, oi.purchasedPrice, o.datePlaced " +
//                    "FROM OrderItemHibernate oi " +
//                    "JOIN oi.orderHibernate o " +
//                    "WHERE o.userHibernate.userId = :userId " +
//                    "AND o.orderStatus != 'Canceled' " +
//                    "ORDER BY o.datePlaced DESC, oi.productHibernate.productId ASC";
//
//            Query<Object[]> query = session.createQuery(hql, Object[].class)
//                    .setParameter("userId", userId)
//                    .setMaxResults(3);
//
//            return query.list().stream()
//                    .map(row -> new OrderItemDTO(
//                            (Long) row[0],   // productId
//                            (String) row[1], // productName
//                            ((Integer) row[2]), // quantity
//                            ((double) row[3]),  //
//                            ((LocalDateTime) row[4])
//                    ))
//                    .collect(Collectors.toList());
//
//        } finally {
//            session.close();
//        }
//    }
    @Transactional
    public List<OrderItemDTO> getTopRecentItems(String authenticatedUsername) {
        Session session = orderDao.openSession();
        try {
            UserHibernate user = userDao.findByUsername(authenticatedUsername);
            if (user == null) {
                throw new IllegalArgumentException("User not found.");
            }

            String hql = "SELECT oi.productHibernate.productId, oi.productHibernate.name, oi.quantity, oi.purchasedPrice, o.datePlaced " +
                    "FROM OrderItemHibernate oi " +
                    "JOIN oi.orderHibernate o " +
                    "WHERE o.userHibernate.userId = :userId " +
                    "AND o.orderStatus != 'Canceled' " +
                    "ORDER BY o.datePlaced DESC, oi.productHibernate.productId ASC";

            Query<Object[]> query = session.createQuery(hql, Object[].class)
                    .setParameter("userId", user.getUserId())
                    .setMaxResults(3);

            return query.list().stream()
                    .map(row -> new OrderItemDTO(
                            (Long) row[0],
                            (String) row[1],
                            ((Integer) row[2]),
                            ((double) row[3]),
                            ((LocalDateTime) row[4])
                    ))
                    .collect(Collectors.toList());

        } finally {
            session.close();
        }
    }
    @Transactional
    public OrderDTO getOrderDetails(Long orderId, String authenticatedUsername) {
        Session session = orderDao.openSession();
        try {
            OrderHibernate order = session.get(OrderHibernate.class, orderId);

            if (order == null) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }

            //  Ensure only the owner can view order details
            if (!order.getUserHibernate().getUsername().equals(authenticatedUsername)) {
                throw new IllegalArgumentException("You are not authorized to view this order.");
            }

            return new OrderDTO(
                    order.getOrderId(),
                    order.getDatePlaced(),
                    order.getOrderStatus(),
                    order.getOrderItems().stream()
                            .map(item -> new OrderItemDTO(
                                    item.getProductHibernate().getProductId(),
                                    item.getProductHibernate().getName(),
                                    item.getQuantity(),
                                    item.getPurchasedPrice()
                            ))
                            .collect(Collectors.toList())
            );
        } finally {
            session.close();
        }
    }

 }
