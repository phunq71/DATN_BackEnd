package com.main.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor @NoArgsConstructor @Data
public class Voucher {
    private String voucherId;
    private Boolean discountType;
    private Integer discountValue;
    private BigDecimal minOrderValue;
    private Integer quantityUsed;
    private Integer quantityRemaining;
    private Date endDate;
    private BigDecimal claimConditions;
}
