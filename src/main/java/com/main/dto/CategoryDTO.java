package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private String id;
    private String name;

    private String categoryID;
    private String categoryName;
    private String parentID;
    private List<CategoryDTO> children;


    public CategoryDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryDTO(String categoryID, String categoryName, String parentID, List<CategoryDTO> children) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.parentID = parentID;
        this.children = children;
    }

}
