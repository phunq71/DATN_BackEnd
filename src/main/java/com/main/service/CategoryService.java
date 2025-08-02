package com.main.service;

import com.main.dto.CategoryDTO;
import com.main.entity.Category;
import com.main.dto.CategoryMenuDTO;


import java.util.List;

public interface CategoryService {
    Category findByCategoryId(String categoryId);
    String findNameById(String id);

    List<CategoryMenuDTO> getCategoryMenu();

    List<CategoryDTO> getAllCategoriesAsTree();
    CategoryDTO createCategory(CategoryDTO dto);
    CategoryDTO updateCategory(String id, CategoryDTO dto);
    void deleteCategory(String id);
    List<CategoryDTO> searchByName(String keyword);
}
