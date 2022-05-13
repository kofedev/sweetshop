package dev.kofe.repo;

import dev.kofe.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemInOrderRepository extends JpaRepository<ItemInOrder, Long> {

    void deleteAllByOrder(Order order);
    void deleteAllByItem (Item item);
    List<ItemInOrder> getAllByOrder(Order order);
    List<ItemInOrder> getAllByItem(Item item);

}
