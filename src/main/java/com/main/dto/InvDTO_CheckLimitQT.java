package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvDTO_CheckLimitQT {
    private Integer itemId;
    private Integer quantity;
    private Integer maxQT;
}
