package dev.kofe.repo;

import dev.kofe.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getAllByQuantityNot (int quantity);
    long countAllByQuantityNot(int quantity);
}
