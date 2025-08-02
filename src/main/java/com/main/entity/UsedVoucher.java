package com.main.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Used_vouchers")
public class UsedVoucher implements Serializable {
    @EmbeddedId
    private UsedVoucherID usedVoucherID;

    @Column(nullable = false)
    private Boolean type;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("voucher")
    @JoinColumn(name = "VoucherID")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customer")
    @JoinColumn(name = "CustomerID")
    private Customer customer;
}
