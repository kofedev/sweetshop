package dev.kofe.service;

import dev.kofe.model.Cart;
import dev.kofe.model.User;

public interface CartService {

    Cart getCartByUser(User user);

    void deleteCart (Cart cart);

    void saveCart(Cart cart);

    Cart getCartByCookieToken(String cookieToken);

}
