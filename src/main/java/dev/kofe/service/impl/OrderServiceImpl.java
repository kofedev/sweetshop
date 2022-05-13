package dev.kofe.service.impl;

import dev.kofe.model.Cart;
import dev.kofe.model.Order;
import dev.kofe.repo.OrderRepository;
import dev.kofe.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order getOrderByCookie (String cookieToken) {
        return orderRepository.getByCookieToken(cookieToken);
    }

    @Transactional
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public Order getOrderByCart (Cart cart) {
        return orderRepository.getByCart(cart);
    }

    @Transactional
    public void deleteOrder (Order order) {
        orderRepository.delete(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(long id) {
        return orderRepository.getById(id);
    }

    public List<Order> getAllOrderInOrderingByDateOfCreation() {
        return orderRepository.findByOrderByDateOfCreation();
    }

}
