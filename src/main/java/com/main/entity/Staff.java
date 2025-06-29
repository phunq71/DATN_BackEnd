package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "staffs")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Staff implements Serializable {
    @Id
    @Column(length = 12)
    private String staffID;

    @OneToOne
    @MapsId
    @JoinColumn(name = "staffId")
    private Account account;


    @Column(length = 55, nullable = false)
    private String fullname;

    @Column(length = 10, nullable = false)
    private String role;

    @Column(length = 10, nullable = false, unique = true)
    private String phone;

    @Column(length = 150, nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDate dob;

    @ManyToOne
    @JoinColumn(name = "facilityId")
    private Facility facility;

    @OneToMany(mappedBy = "staff")
    private List<Facility> facilities;

    @OneToMany(mappedBy = "staff")
    private List<InventorySlip> inventorySlips;

    @OneToMany(mappedBy = "approver")
    private List<InventorySlip> approver_inventorSlips;

    @OneToMany(mappedBy = "staff")
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "approver")
    private List<Transaction> approver_transactions;

    @OneToMany(mappedBy = "staff")
    private List<ReturnRequest> returRequests;

    @OneToMany(mappedBy = "staff")
    private List<Order> orders;

}
