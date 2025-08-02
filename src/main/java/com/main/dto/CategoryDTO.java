package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO {
    private String categoryID;
    private String categoryName;
    private String parentID;
    private List<CategoryDTO> children;
}
