package com.main.entity;


import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "Used_vouchers")
@IdClass(UsedVoucherID.class)
public class UsedVoucher implements Serializable {
    @EmbeddedId
    private UsedVoucherID usedVoucherID;

    @Column(nullable = false)
    private Boolean type;

    @ManyToOne
    @MapsId("voucher")
    @JoinColumn(name = "VoucherID")
    private Voucher voucher;

    @ManyToOne
    @MapsId("customer")
    @JoinColumn(name = "CustomerID")
    private Customer customer;
}
