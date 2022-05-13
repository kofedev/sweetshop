package dev.kofe.model;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // consumer matter

    @Column
    private String factura;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private String phone;

    @Column
    private String notes;

    @Column
    private String email;

    // link to card
    @OneToOne
    private Cart cart;

    // items matter

    @OneToOne
    private ItemInOrder itemInOrder;

    // time matter

    @Column
    private java.sql.Date time =  new Date(System.currentTimeMillis());

    // business

    @Column
    private BigDecimal total_netto = new BigDecimal(0);;

    @Column
    private BigDecimal vat_in_percent = new BigDecimal(0);;

    @Column
    private BigDecimal vat_value = new BigDecimal(0);;

    @Column
    private BigDecimal total_brutto = new BigDecimal(0);;

    @Column
    private BigDecimal delivery_price_netto = new BigDecimal(0);

    // status and cookie

    @Column
    private OrderStatus status = OrderStatus.CREATED;

    @Column
    private String cookieToken = "";

    @Column
    private java.util.Date dateOfCreation = new java.util.Date();

    @Column
    private BigDecimal total_netto_without_delivery;

}
