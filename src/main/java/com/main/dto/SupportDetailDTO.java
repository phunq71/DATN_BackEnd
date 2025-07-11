package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class SupportDetailDTO {
    private Byte discountPercent;
    private Boolean isFavorite;
    private Boolean isHot;
    private Boolean isNew;
    private long soldQuantity;
}
