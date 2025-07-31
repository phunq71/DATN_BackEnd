package com.main.entity;



import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "InventorySlipDetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventorySlipDetail implements Serializable {

    @EmbeddedId
    private InventorySlipDetailId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId")
    @JoinColumn(name = "ItemID", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("inventorySlipId")
    @JoinColumn(name = "InventorySlipID", nullable = false)
    private InventorySlip inventorySlip;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;
}


