package dev.kofe.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private User user;

    @OneToOne
    private ItemInCart itemInCart;

    // link to the order
    @OneToOne
    private Order order = null;

    // business

    @Column
    BigDecimal total = new BigDecimal(0); // total money BEFORE TAXES

    @Column
    int currentQuantityAllItems = 0;

    @Transient
    BigDecimal discount; // reserve

    @Transient
    BigDecimal grandTotal = new BigDecimal(0);; // total AFTER TAXES

    // cookie

    @Column
    String cookieToken = "";

    public Cart (String cookieToken) {
        this.cookieToken = cookieToken;
    }

    public Cart () {

    }

}
