package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartDTO {
    private String customerID;
    private String itemID;
    private int quantity;
    private LocalDateTime latestDate;
}
