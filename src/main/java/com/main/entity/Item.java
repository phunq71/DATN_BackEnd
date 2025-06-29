package com.main.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ItemID")
    private Integer itemId;

    @Column(name = "VariantID", nullable = false, length = 13)
    private String variantId;

    @ManyToOne
    @JoinColumn(name = "SizeID")
    private Size sizes;

    @OneToMany(mappedBy = "item")
    private List<InventorySlipDetail> inventorySlipDetails;

    @OneToMany(mappedBy = "item")
    private List<Inventory> inventories;

    @OneToMany(mappedBy = "item")
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "item")
    private List<Cart> carts;
}

