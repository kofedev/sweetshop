package dev.kofe.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Delivery is a delivery option (for instance, "standard, 1 Euro")
 */

@Data
@Entity
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String description;

    @Column
    private BigDecimal price; // netto value

    @Column
    private String notes;

}
