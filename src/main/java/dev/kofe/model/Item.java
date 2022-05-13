package dev.kofe.model;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;

@Data
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String title;

    @Lob
    @Column(length = 3000)
    @Basic(fetch = FetchType.LAZY)
    private String description;

    @Column
    private BigDecimal price; // origin current price in Euro

    @Column
    private int quantity;

    @Lob
    private byte[] photo = null;

    @OneToMany(fetch = FetchType.LAZY)
    private Collection<ItemInCart> itemInCarts;

    @OneToMany(fetch = FetchType.LAZY)
    private Collection<ItemInOrder> itemInOrders;

    @Transient
    private String photoAsAString = null;

    @Transient
    private int chosenQuantity = 0;

    @Transient
    private int index = 0;

    @Transient
    private int max = 0; // for input field

}
