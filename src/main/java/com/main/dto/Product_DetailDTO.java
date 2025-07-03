package com.main.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class Product_DetailDTO {
    String productId;
    String productName;
    List<Variant_DetailDTO> variants;
}
