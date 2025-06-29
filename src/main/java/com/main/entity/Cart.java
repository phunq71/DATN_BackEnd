package com.main.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart implements Serializable {

    @EmbeddedId
    private CartId id;

    @ManyToOne
    @MapsId("customer")
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @ManyToOne
    @MapsId("item")
    @JoinColumn(name = "ItemID", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "LatestDate", nullable = false)
    private LocalDateTime latestDate;
}

