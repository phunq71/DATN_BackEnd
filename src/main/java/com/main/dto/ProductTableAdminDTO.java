package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @NoArgsConstructor
@AllArgsConstructor
public class ProductTableAdminDTO {
    private String id;
    private String name;
    private LocalDate createdDate;
    private String targetCustomer;
    private String brand;
    private String image;
    private BigDecimal price;
    private BigDecimal discount;
}
