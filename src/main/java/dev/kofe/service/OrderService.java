package dev.kofe.service;

import dev.kofe.model.Cart;
import dev.kofe.model.Order;
import java.util.List;

public interface OrderService {

    Order getOrderByCookie (String cookieToken);

    void saveOrder(Order order);

    Order getOrderByCart (Cart cart);

    void deleteOrder (Order order);

    List<Order> getAllOrders();

    Order getOrderById(long id);

    List<Order> getAllOrderInOrderingByDateOfCreation();

}
