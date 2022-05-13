package dev.kofe.repo;

import dev.kofe.model.Cart;
import dev.kofe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart getByUser(User user);
    Cart getByCookieToken (String cookieToken);

}
