package com.main.serviceImpl;

import com.main.entity.Category;
import com.main.dto.CategoryMenuDTO;
import com.main.mapper.CategoryMapper;
import com.main.repository.CategoryFlatResult;
import com.main.repository.CategoryRepository;
import com.main.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Override
    public Category findByCategoryId(String categoryId) {
        return categoryRepository.findByCategoryId(categoryId);

    }

    //tìm kiếm danh mục theo id
    @Override
    public String findNameById(String id) {
        return categoryRepository.findById(id)
                .map(Category::getCategoryName)
                .orElse(null);
    }

    @Override
    public List<CategoryMenuDTO> getCategoryMenu() {
        List<CategoryFlatResult> flatResults = categoryRepository.fetchCategoryWithChildren();
        return CategoryMapper.toCategoryMenuDTO(flatResults);
    }
}
