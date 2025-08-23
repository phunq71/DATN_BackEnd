package com.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryDTO {
    private String id;
    private String name;

    private String categoryID;
    private String categoryName;

    private String content;
    private String banner;

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

    public CategoryDTO(String categoryID, String categoryName, String parentID, String content, String banner, List<CategoryDTO> children) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.parentID = parentID;
        this.content = content;
        this.banner = banner;
        this.children = children;
    }



}
