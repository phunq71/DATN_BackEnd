package com.main.dto;

import com.main.entity.CartId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemCartDTO {
    private CartId cartId;
    private Integer itemID;
    private String productName;
    private String variantID;
    private String Color;
    private Integer sizeID;
    private String sizeCode;
    private BigDecimal price;
    private String mainImage;
}
