package com.main.serviceImpl;

import com.main.dto.Variant_DetailDTO;
import com.main.entity.Product;
import com.main.entity.Variant;
import com.main.mapper.VariantMapper;
import com.main.repository.VariantRepository;
import com.main.service.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VariantServiceImpl implements VariantService {
    @Autowired
    private VariantRepository variantRepository;
    @Override
    public List<Variant_DetailDTO> findByProduct(Product product) {
        return variantRepository.findByProductAndIsUseTrue(product)
                .stream()
                .map(VariantMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    public Optional<Variant> findById(String variant) {
        return variantRepository.findById(variant);
    }
}
