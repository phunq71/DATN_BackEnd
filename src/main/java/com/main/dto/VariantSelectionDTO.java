package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantSelectionDTO {
    private String sizeCode;
    private String variantID;
    private boolean checked;
}

