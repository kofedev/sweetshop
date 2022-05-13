package dev.kofe.service;

import dev.kofe.model.Item;
import dev.kofe.model.ItemInOrder;
import dev.kofe.model.Order;
import java.util.List;

public interface ItemInOrderService {

    void saveItemInOrder (ItemInOrder itemInOrder);

    void deleteAllByOrder (Order order);

    void deleteAllByItem (Item item);

    List<ItemInOrder> getAllItemInOrderByOrder (Order order);

    List<ItemInOrder> getAllItemInOrderByItem (Item item);

}
