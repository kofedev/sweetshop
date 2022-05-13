package dev.kofe.service.impl;

import dev.kofe.model.Item;
import dev.kofe.model.ItemInOrder;
import dev.kofe.model.Order;
import dev.kofe.repo.ItemInOrderRepository;
import dev.kofe.service.ItemInOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemInOrderServiceImpl implements ItemInOrderService {

    @Autowired
    private ItemInOrderRepository itemInOrderRepository;

    @Transactional
    public void saveItemInOrder (ItemInOrder itemInOrder) {
        itemInOrderRepository.save(itemInOrder);
    }

    @Transactional
    public void deleteAllByOrder (Order order) {
        itemInOrderRepository.deleteAllByOrder(order);
    }

    @Transactional
    public void deleteAllByItem (Item item) { itemInOrderRepository.deleteAllByItem(item); }

    public List<ItemInOrder> getAllItemInOrderByOrder (Order order) {
        return itemInOrderRepository.getAllByOrder(order);
    }
    public List<ItemInOrder> getAllItemInOrderByItem (Item item) {
        return itemInOrderRepository.getAllByItem(item);
    }

}
