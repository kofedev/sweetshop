package dev.kofe.repo;

import dev.kofe.model.Cart;
import dev.kofe.model.Item;
import dev.kofe.model.ItemInCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemInCartRepository extends JpaRepository<ItemInCart, Long> {

    ItemInCart getByItem(Item item);
    ItemInCart getByItemAndCart(Item item, Cart cart);
    void deleteAllByItem(Item item);
    void deleteAllByCart(Cart cart);
    List<ItemInCart> getAllByCart(Cart cart);

}
