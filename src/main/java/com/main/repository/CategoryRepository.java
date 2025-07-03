package com.main.repository;

import com.main.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, String> {
    @Query("""
        SELECT p.categoryId AS parentId, p.categoryName AS parentName,
               c.categoryId AS childId, c.categoryName AS childName
        FROM Category p
        LEFT JOIN Category c ON c.parent.categoryId = p.categoryId
        WHERE p.parent IS NULL
    """)
    List<CategoryFlatResult> fetchCategoryWithChildren();
}
