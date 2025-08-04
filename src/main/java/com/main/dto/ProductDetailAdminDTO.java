package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailAdminDTO {
    private String id;
    private String name;
    private String description;
    private LocalDate createdDate;
    private String targetCustomer;
    private String brand;
    private String categoryName;
    private List<VariantSelectionDTO> variantSizes;
    private List<VariantProDetailAdminDTO> listVariants;
}
