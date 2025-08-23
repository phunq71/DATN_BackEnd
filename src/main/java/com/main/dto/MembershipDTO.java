package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MembershipDTO {
    private String id;
    private String rank;
    private String description;
    private BigDecimal minPoint;

    private String code;
    private String name;

    public MembershipDTO(String id, String rank, String description, BigDecimal minPoint) {
        this.id = id;
        this.rank = rank;
        this.description = description;
        this.minPoint = minPoint;
    }

    public MembershipDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
