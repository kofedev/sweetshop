package dev.kofe.service.impl;

import dev.kofe.model.Cart;
import dev.kofe.model.Item;
import dev.kofe.model.ItemInCart;
import dev.kofe.repo.ItemInCartRepository;
import dev.kofe.service.ItemInCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemInCardServiceImpl implements ItemInCardService {

    @Autowired
    private ItemInCartRepository itemInCartRepository;

    public ItemInCart getById (Long id) {
        Optional<ItemInCart> optionalItemInCart = itemInCartRepository.findById(id);
        ItemInCart itemInCart;
        if (optionalItemInCart.isPresent()) {
            itemInCart = optionalItemInCart.get();
        } else {
            itemInCart = null;
        }

        return itemInCart;
    }

    public ItemInCart getByItem (Item item) {
        return itemInCartRepository.getByItem(item);
    }

    public ItemInCart getByItemAndCart (Item item, Cart cart) {
        return itemInCartRepository.getByItemAndCart(item, cart);
    }

    @Transactional
    public void saveItemInCart(ItemInCart item) {
        itemInCartRepository.save(item);
    }

    @Transactional
    public void deleteAllByItem (Item item) {
        itemInCartRepository.deleteAllByItem(item);
    }

    // typo with 'd' - is a signature :)
    public List<ItemInCart> getAllByCard (Cart cart) {
        return itemInCartRepository.getAllByCart(cart);
    }

    @Transactional
    public void deleteAllByCart (Cart cart) {
        itemInCartRepository.deleteAllByCart(cart);
    }

}
