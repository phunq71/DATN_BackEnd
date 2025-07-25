package com.main.service;

import com.main.dto.Image_DetailDTO;
import com.main.entity.Variant;

import java.util.List;

public interface ImageService {
    List<Image_DetailDTO> findByVariant(Variant variant);
}
