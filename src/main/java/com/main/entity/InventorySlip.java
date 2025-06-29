package com.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "InventorySlips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventorySlip implements Serializable {

    @Id
    @Column(name = "ISID", length = 10)
    private String isid;

    @Column(name = "Type", nullable = false)
    private Boolean type; // true = nhập, false = xuất

    // Cơ sở xuất hàng
    @ManyToOne
    @JoinColumn(name = "FromFID")
    private Facility fromFacility;

    // Cơ sở nhập hàng
    @ManyToOne
    @JoinColumn(name = "ToFID")
    private Facility toFacility;

    @Column(name = "CreateDate", nullable = false)
    private LocalDateTime createDate;

    // Nhân viên lập phiếu
    @ManyToOne
    @JoinColumn(name = "StaffID", nullable = false)
    private Staff staff;

    // Người duyệt (có thể null)
    @ManyToOne
    @JoinColumn(name = "ApproverID")
    private Staff approver;

    @Column(name = "Note", length = 500)
    private String note;

    @OneToMany(mappedBy = "Inventory")
    private List<InventorySlipDetail> inventorySlipDetails;
}

