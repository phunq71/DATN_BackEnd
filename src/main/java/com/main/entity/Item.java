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

    @ManyToOne
    @JoinColumn(name = "VarianID")
    private Variant variant;

    @ManyToOne
    @JoinColumn(name = "SizeID")
    private Size size;

    @OneToMany(mappedBy = "item")
    private List<InventorySlipDetail> inventorySlipDetails;

    @OneToMany(mappedBy = "item")
    private List<Inventory> inventories;

    @OneToMany(mappedBy = "item")
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "item")
    private List<Cart> carts;
}

