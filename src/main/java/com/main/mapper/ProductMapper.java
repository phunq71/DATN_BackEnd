package com.main.mapper;

import com.main.dto.Product_DetailDTO;
import com.main.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    //mapper gộp cả variant đc map
    public static Product_DetailDTO toDTO(Product product) {
        if (product == null) return null;
        Product_DetailDTO dto = new Product_DetailDTO();
        dto.setProductId(product.getProductID());
        dto.setProductName(product.getProductName());
        dto.setVariants(VariantMapper.toDTOList(product.getVariants()));

        return dto;
    }
}
