package com.main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherOrderDTO {
    private String voucherID;

    private String discountType;  // BIT -> Boolean

    private Integer discountValue;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime endDate;

    private BigDecimal minOrderValue;

    private Boolean isUse;

    private Boolean type;

    private BigDecimal claimConditions;
}
