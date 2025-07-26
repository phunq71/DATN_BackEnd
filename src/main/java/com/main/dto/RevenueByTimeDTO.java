package com.main.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RevenueByTimeDTO {
    private Integer time;
    private Long orderCount;
    private BigDecimal revenue;

}
