package com.main.service;

import com.main.dto.CategoryMenuDTO;

import java.util.List;

public interface CategoryService {
    String findNameById(String id);

    List<CategoryMenuDTO> getCategoryMenu();
}
