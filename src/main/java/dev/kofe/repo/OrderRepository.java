package dev.kofe.repo;

import dev.kofe.model.Cart;
import dev.kofe.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order getByCookieToken (String cookieToken);
    Order getByCart(Cart cart);
    List<Order> findByOrderByDateOfCreation();

}
