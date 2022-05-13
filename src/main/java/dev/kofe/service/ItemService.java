package dev.kofe.service;

import dev.kofe.model.Item;
import java.util.List;

public interface ItemService {

    List<Item> getAll();

    List<Item> getAllWhichQuantityIsNotZero();

    void saveItem(Item item);

    void deleteItem (Item item);

    Item getById (Long id);

    long getCountAllWhichQuantityIsNotZero();

}
