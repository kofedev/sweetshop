package dev.kofe.service.utility;

import dev.kofe.model.*;
import dev.kofe.service.ItemInCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Util {

    @Autowired
    private ItemInCardService itemInCardService;

    public void updateCurrentQuantityAllItemsInCart (Cart cart) {

        int quantity = 0;

        List<ItemInCart> itemInCartList = itemInCardService.getAllByCard(cart);
        if (itemInCartList != null) {
            for (ItemInCart itemInCart : itemInCartList) {
                quantity += itemInCart.getChosenQuantity();
            }
        } else {
            quantity = 0;
        }

        cart.setCurrentQuantityAllItems(quantity);
    }

    public int getAllowedMaximumForItemConsiderCart (Item item, Cart cart) {

        int result = 0;

        if (item == null || cart == null) {} // @ToDo

        ItemInCart itemInCart = itemInCardService.getByItemAndCart(item, cart);

        if (itemInCart == null) {
            result = item.getQuantity(); // there is not the item in the cart yet
        } else {
            result = item.getQuantity() - itemInCart.getChosenQuantity();
            if (result < 0) result = 0;
        }

        return result;
    }

}
