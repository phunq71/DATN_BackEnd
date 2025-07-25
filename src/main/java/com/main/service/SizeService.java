package com.main.service;

import com.main.dto.SizeDTO;

import java.util.List;

public interface SizeService {
    List<SizeDTO> getSizeDTOByVariantID(String variantId);
}
