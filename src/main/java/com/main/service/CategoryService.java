package com.main.service;

import com.main.dto.CategoryDTO;
import com.main.entity.Category;
import com.main.dto.CategoryMenuDTO;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

public interface CategoryService {
    Category findByCategoryId(String categoryId);
    String findNameById(String id);

    List<CategoryMenuDTO> getCategoryMenu();

    List<CategoryDTO> getAllCategoriesAsTree();
    CategoryDTO createCategory(CategoryDTO dto, MultipartFile file) throws IOException;
    CategoryDTO updateCategory(String id, CategoryDTO dto, MultipartFile file) throws IOException;
    void deleteCategory(String id);
    List<CategoryDTO> searchByName(String keyword);
}
