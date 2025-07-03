package com.main.repository;

import com.main.entity.Category;
import com.main.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, String> {
    Category findByCategoryId(String categoryId);
}
