package dev.kofe.service;

import dev.kofe.model.Cart;
import dev.kofe.model.Item;
import dev.kofe.model.ItemInCart;
import java.util.List;

/**
 * typo 'd' in the word "card" is a "signature" :)) instead of "cart"
 */
public interface ItemInCardService {

    ItemInCart getById (Long id);

    ItemInCart getByItem (Item item);

    ItemInCart getByItemAndCart (Item item, Cart cart);

    void saveItemInCart(ItemInCart item);

    void deleteAllByItem (Item item);

    List<ItemInCart> getAllByCard (Cart cart);

    void deleteAllByCart (Cart cart);

}
