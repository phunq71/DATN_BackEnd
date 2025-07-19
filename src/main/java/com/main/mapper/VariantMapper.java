package com.main.mapper;

import com.main.dto.Variant_DetailDTO;
import com.main.entity.Variant;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VariantMapper {
    public static Variant_DetailDTO toDTO(Variant variant) {
        if (variant == null) return null;
        Variant_DetailDTO dto = new Variant_DetailDTO();
        dto.setVariantId(variant.getVariantID());
        dto.setColor(variant.getColor());
        dto.setDescription(variant.getDescription());
        dto.setPrice(variant.getPrice());
        dto.setImages(variant.getImages()); // nếu dùng chung Image entity
        variant.getImages().forEach(image -> {
            if(image.getIsMainImage()) {
                dto.setMainImage(image.getImageUrl());
            }
        });
        dto.setItems(ItemMapper.toDTOList(variant.getItems()));

        return dto;
    }
    public static List<Variant_DetailDTO> toDTOList(List<Variant> variants) {
        if (variants == null) return Collections.emptyList();

        return variants.stream()
                .map(VariantMapper::toDTO)
                .collect(Collectors.toList());
    }
}
