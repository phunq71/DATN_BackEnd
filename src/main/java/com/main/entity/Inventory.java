package com.main.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "Inventories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory implements Serializable {

    @EmbeddedId
    private InventoryId id;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "ItemID")
    private Item item;

    @ManyToOne
    @MapsId("facilityId")
    @JoinColumn(name = "FacilityID")
    private Facility facility;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "MinQT")
    private Integer minQt;

    @Column(name = "MaxQT")
    private Integer maxQt;
}

