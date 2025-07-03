package com.main.service;

import com.main.entity.Category;
import com.main.entity.Product;

import java.util.List;

public interface CategoryService {
    Category findByCategoryId(String categoryId);
}
