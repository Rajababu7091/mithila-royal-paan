package com.mithila.royalpaan.service;

import com.mithila.royalpaan.entity.Order;
import com.mithila.royalpaan.entity.Product;
import com.mithila.royalpaan.entity.Notification;
import com.mithila.royalpaan.entity.User;
import com.mithila.royalpaan.repository.OrderRepository;
import com.mithila.royalpaan.repository.ProductRepository;
import com.mithila.royalpaan.repository.NotificationRepository;
import com.mithila.royalpaan.dto.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public Order createOrder(OrderRequest req, User customer) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getAvailability()) {
            throw new RuntimeException("Product is currently unavailable");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setProduct(product);
        order.setQuantity(req.getQuantity());
        order.setShippingAddress(req.getShippingAddress());
        order.setPhone(req.getPhone());
        order.setStatus("PENDING");

        // Calculate total price: price * quantity
        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(req.getQuantity()));
        order.setPrice(totalPrice);

        Order saved = orderRepository.save(order);

        // Notify customer
        notificationRepository.save(new Notification(customer, 
            "Your order for " + req.getQuantity() + "x " + product.getName() + " (Total: INR " + totalPrice + ") was successfully created. Status: PENDING."));

        return saved;
    }

    public List<Order> getOrdersByCustomer(User customer) {
        return orderRepository.findByCustomerId(customer.getId());
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Integer id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        Order updated = orderRepository.save(order);

        // Notify customer
        notificationRepository.save(new Notification(order.getCustomer(), 
            "Your order (ID: " + id + ") status has been updated to: " + status + "."));

        return updated;
    }
}
