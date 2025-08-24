package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherManagermetDTO {
    private String voucherId;
    private String discountType;
    private Integer discountValue;
    private BigDecimal minOrderValue;
    private Integer quantityUsed;
    private Integer quantityRemaining;
    private LocalDateTime endDate;
    private BigDecimal claimConditions;
    private String promotionId;
}
