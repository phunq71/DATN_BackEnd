package com.main.service;

import com.main.entity.Category;
import com.main.dto.CategoryMenuDTO;


import java.util.List;

public interface CategoryService {
    Category findByCategoryId(String categoryId);
    String findNameById(String id);

    List<CategoryMenuDTO> getCategoryMenu();

}
