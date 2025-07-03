package com.main.dto;

import com.main.entity.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class Item_DetailDTO {
    int itemID;
    String variantID;
    Integer sizeID;
    List<Size> sizes;
}
