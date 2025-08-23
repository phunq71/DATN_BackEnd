package com.main.rest_controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.dto.CategoryDTO;
import com.main.service.CategoryService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class CategoryRestController {
    private final CategoryService categoryService;

    private void checkAdmin() {
        if (!AuthUtil.isLogin() || !"ROLE_ADMIN".equals(AuthUtil.getRole())) {
            throw new SecurityException("Bạn không có quyền thực hiện chức năng này.");
        }
    }

    // Lấy danh sách danh mục dạng cây
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        checkAdmin(); // chỉ admin mới được truy cập
        List<CategoryDTO> list = categoryService.getAllCategoriesAsTree();
        return ResponseEntity.ok(list);
    }

    // Tạo mới danh mục
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestParam("category") String categoryJson,
                                                      @RequestParam(value = "file", required = false) MultipartFile file) throws JsonProcessingException {
        checkAdmin();
        CategoryDTO created = null;
        CategoryDTO dto = new ObjectMapper().readValue(categoryJson, CategoryDTO.class);
        try {
            created = categoryService.createCategory(dto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(created);
    }

    // Cập nhật danh mục
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable String id,
            @RequestParam("category") String categoryJson,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        checkAdmin();
        CategoryDTO updated = null;
        CategoryDTO dto = new ObjectMapper().readValue(categoryJson, CategoryDTO.class);
        try {
            updated = categoryService.updateCategory(id, dto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(updated);
    }

    // Xóa danh mục
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable String id) {
        checkAdmin();
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Tìm kiếm danh mục theo tên
    @GetMapping("/search")
    public ResponseEntity<List<CategoryDTO>> searchByName(@RequestParam String name) {
        checkAdmin();
        List<CategoryDTO> list = categoryService.searchByName(name);
        return ResponseEntity.ok(list);
    }
}
