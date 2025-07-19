package com.main.service;

import com.main.dto.Variant_DetailDTO;
import com.main.entity.Product;
import com.main.entity.Variant;

import java.util.List;
import java.util.Optional;

public interface VariantService {
    List<Variant_DetailDTO> findByProduct(Product product);
    Optional<Variant> findById(String variant);
}
