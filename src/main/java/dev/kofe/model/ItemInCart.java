package dev.kofe.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ItemInCart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Item item;

    @OneToOne
    private Cart cart;

    private int chosenQuantity = 0;

}
