package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantProDetailAdminDTO {
    private String id;
    private String color;
    private String description;
    private LocalDate createdDate;
    private BigDecimal price;
    private BigDecimal discount;
    private Boolean isMainVariant;
    private Boolean isUse;
    private List<ImageDTO> images;
}
