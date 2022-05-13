package dev.kofe.service.impl;

import dev.kofe.model.Cart;
import dev.kofe.model.User;
import dev.kofe.repo.CartRepository;
import dev.kofe.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    public Cart getCartByUser(User user) {
        return cartRepository.getByUser(user);
    }

    @Transactional
    public void deleteCart (Cart cart) {
        cartRepository.delete(cart);
    };

    @Transactional
    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }

    public Cart getCartByCookieToken(String cookieToken) {
        return cartRepository.getByCookieToken(cookieToken);
    }

}
