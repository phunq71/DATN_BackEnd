package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AreaDTO {
    private String areaID;
    private String areaName;
    private String shopID;
    private String shopName;
    private String managerId;
}
