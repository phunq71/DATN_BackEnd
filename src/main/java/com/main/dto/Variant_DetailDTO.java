package com.main.dto;

import com.main.entity.Image;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
@Setter
@Getter
public class Variant_DetailDTO {
    String variantId;
    String color;
    String description;
    BigDecimal price;
    List<Image> images;
    List<Item_DetailDTO> items;
    String imagesDTO;
    String mainImage;
}
