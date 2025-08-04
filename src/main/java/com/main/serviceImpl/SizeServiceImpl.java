package com.main.serviceImpl;

import com.main.dto.SizeDTO;
import com.main.repository.SizeRepository;
import com.main.service.SizeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SizeServiceImpl implements SizeService {
    private final SizeRepository sizeRepository;

    public SizeServiceImpl(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @Override
    public List<SizeDTO> getSizeDTOByVariantID(String variantId) {

        List<SizeDTO> sizeDTOs =sizeRepository.getSizeDTOByVariantID(variantId);

        sizeDTOs.forEach(size -> {
            size.setIsInStock(size.getStockQuantity() > 0);
        });

        return sizeDTOs;
    }




}
