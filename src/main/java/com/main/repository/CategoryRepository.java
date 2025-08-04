package com.main.repository;

import com.main.dto.CategoryDTO;
import com.main.entity.Category;
import com.main.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, String> {
    Category findByCategoryId(String categoryId);
    @Query("""
        SELECT p.categoryId AS parentId, p.categoryName AS parentName,
               c.categoryId AS childId, c.categoryName AS childName
        FROM Category p
        LEFT JOIN Category c ON c.parent.categoryId = p.categoryId
        WHERE p.parent IS NULL
    """)
    List<CategoryFlatResult> fetchCategoryWithChildren();

    @Query("SELECT new com.main.dto.CategoryDTO(c.categoryId, c.categoryName) " +
            "FROM Category c WHERE c.parent IS NOT NULL")
    List<CategoryDTO> findAllRootCategories();

    Category findByCategoryName(String categoryName);
}
