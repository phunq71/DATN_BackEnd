package com.main.serviceImpl;

import com.main.dto.ProductByCategory;
import com.main.entity.Product;
import com.main.mapper.ProductMapper;
import com.main.repository.ProductRepository;
import com.main.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;


    //hiển thị sp theo dm có phân trang
    @Override
    public Page<ProductByCategory> getProductsByCategory(String parentId, String childId, int page) {
        Pageable pageable = PageRequest.of(page, 12); // 12 sản phẩm/trang

        Page<Product> productPage;

        if (childId == null || childId.equals("null")) {
            productPage = productRepository.findByParentCategoryOnly(parentId, pageable);//lọc theo dm cha
        } else {
            productPage = productRepository.findByParentAndChildCategory(parentId, childId, pageable);
        }

        //chuyển đổi Page<p> thành page<PbyC>
        return productPage.map(ProductMapper::toDTO);
    }
}
