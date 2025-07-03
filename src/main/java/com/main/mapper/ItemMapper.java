package com.main.mapper;

import com.main.dto.Item_DetailDTO;
import com.main.entity.Item;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    //Xử lý Gửi các thuoc tinh can thiet cho front end
    public static Item_DetailDTO toDTO(Item item) {
        if (item == null) return null;
        Item_DetailDTO dto = new Item_DetailDTO();
        dto.setItemID(item.getItemId());
        dto.setVariantID(item.getVariant().getVariantID());
        dto.setSizeID(item.getSize().getSizeID());
        // Nếu Item chỉ có 1 Size:
        dto.setSizes(Collections.singletonList(item.getSize()));

        // Nếu Item có List<Size>:
        // dto.setSizes(item.getSizes());

        return dto;
    }

    public static List<Item_DetailDTO> toDTOList(List<Item> items) {
        if (items == null) return Collections.emptyList();
        return items.stream()
                .map(ItemMapper::toDTO)
                .collect(Collectors.toList());
    }
}
