package com.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FromFID")
    private Facility fromFacility;

    // Cơ sở nhập hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ToFID")
    private Facility toFacility;

    @Column(name = "Status", nullable = false)
    private String status;

    @Column(name = "CreateDate", nullable = false)
    private LocalDateTime createDate;

    // Nhân viên lập phiếu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StaffID", nullable = false)
    private Staff staff;

    // Người duyệt (có thể null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApproverID")
    private Staff approver;

    @Column(name = "Note", length = 500)
    private String note;

    @OneToMany(mappedBy = "inventorySlip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventorySlipDetail> inventorySlipDetails;

    public InventorySlip(String isid) {
        this.isid = isid;
    }

    public InventorySlip(InventorySlip slip) {
        this.isid = null;
        this.type = slip.getType();
        this.fromFacility = slip.getFromFacility();
        this.toFacility = slip.getToFacility();
        this.status = slip.getStatus();
        this.createDate = slip.getCreateDate();
        this.staff = slip.getStaff();
        this.approver = slip.getApprover();
        this.note = slip.getNote();
        this.inventorySlipDetails = new ArrayList<>();
    }
}

