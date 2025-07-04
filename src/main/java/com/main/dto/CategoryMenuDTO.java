package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMenuDTO {
    private String id;
    private String name;
    private List<ChildCategoryDTO> children;
}

