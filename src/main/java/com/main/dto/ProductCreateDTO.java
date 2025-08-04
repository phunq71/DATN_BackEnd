package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {
    private String id;
    private String name;
    private String description;
    private String targetCustomer;
    private String brand;
    private String categoryName;
    private LocalDate createdDate;
}
