package com.main.mapper;

import com.main.dto.CategoryMenuDTO;
import com.main.dto.ChildCategoryDTO;
import com.main.entity.Category;
import com.main.entity.Product;
import com.main.repository.CategoryFlatResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CategoryMapper {

    // dùng cho menu toàn hệ thống
    public static List<CategoryMenuDTO> toCategoryMenuDTO(List<CategoryFlatResult> flatResults) {
        Map<String, CategoryMenuDTO> grouped = new LinkedHashMap<>();

        for (CategoryFlatResult row : flatResults) {
            String parentId = row.getParentId(); // ✅ phải là String
            String parentName = row.getParentName();

            grouped.computeIfAbsent(parentId, id ->
                    new CategoryMenuDTO(id, parentName, new ArrayList<>()) // ✅ id là String
            );

            if (row.getChildId() != null && row.getChildName() != null) {
                grouped.get(parentId).getChildren().add(
                        new ChildCategoryDTO(row.getChildId(), row.getChildName()) // ✅ cả 2 là String
                );
            }
        }

        return new ArrayList<>(grouped.values());
    }
}
