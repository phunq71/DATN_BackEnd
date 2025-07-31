package com.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Facilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Facility implements Serializable {

    @Id
    @Column(name = "FacilityID", length = 6)
    private String facilityId;

    @Column(name = "Type", nullable = false, length = 1)
    private String type;

    @Column(name = "FacilityName", nullable = false, length = 100)
    private String facilityName;

    @Column(name = "Address", nullable = true, length = 150)
    private String address;

    @Column(name = "AddressIdGHN", nullable = true, length = 100)
    private String addressIdGHN;

    // Quan hệ tự thân: nhiều Facility con có thể có 1 parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ParentID")
    private Facility parent;

    @OneToMany(mappedBy = "parent")
    private List<Facility> childrens;

    // Quản lý bởi 1 người (giả sử là Account)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Manager", nullable = true)
    private Staff manager;

    @OneToMany(mappedBy = "facility")
    private List<Staff> staffList;

    @Column(name = "IsUse", nullable = false)
    private Boolean isUse;

    @OneToMany(mappedBy = "facility")
    private List<Inventory> inventories;

    @OneToMany(mappedBy = "facility")
    private List<Order> orders;

    @OneToMany(mappedBy = "fromFacility")
    private List<InventorySlip> exportReceipts; // Phiếu xuất từ cơ sở này

    @OneToMany(mappedBy = "toFacility")
    private List<InventorySlip> importReceipts; // Phiếu nhập về cơ sở này

    @OneToMany(mappedBy = "facility")
    private List<Transaction> Transactions;
}

