package dev.kofe.service.impl;

import dev.kofe.model.Item;
import dev.kofe.repo.ItemRepository;
import dev.kofe.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public List<Item> getAllWhichQuantityIsNotZero () {
        return itemRepository.getAllByQuantityNot(0);
    }

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void deleteItem (Item item) {
        itemRepository.delete(item);
    }

    public Item getById (Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        Item item;
        if (optionalItem.isPresent()) {
            item = optionalItem.get();
        } else {
            item = null;
        }

        return item;
    }

    public long getCountAllWhichQuantityIsNotZero () {
        return itemRepository.countAllByQuantityNot(0);
    }

}
