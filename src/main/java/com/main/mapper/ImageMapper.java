package com.main.mapper;

import com.main.dto.Image_DetailDTO;
import com.main.entity.Image;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {
    //Xử lý chỉ gửi urlImage cho front-end
    public static Image_DetailDTO toDTO(Image image) {
        if (image == null) return null;
        Image_DetailDTO dto = new Image_DetailDTO();
        dto.setImageUrlDTO(image.getImageUrl());
        return dto;
    }
}
