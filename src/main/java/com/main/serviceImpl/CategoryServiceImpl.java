package com.main.serviceImpl;

import com.main.entity.Category;
import com.main.mapper.CategoryMapper;
import com.main.repository.CategoryRepository;
import com.main.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;


    //tìm kiếm danh mục theo id
    @Override
    public String findNameById(String id) {
        return categoryRepository.findById(id)
                .map(Category::getCategoryName)
                .orElse(null);
    }
}
