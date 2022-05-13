package dev.kofe.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ItemInOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Item item;

    @OneToOne
    private Order order;

    private int orderedQuantity = 0;

}
