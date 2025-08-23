package com.main.rest_controller;


import com.main.dto.*;
import com.main.entity.Category;
import com.main.repository.CategoryRepository;
import com.main.repository.ProductRepository;
import com.main.service.CategoryService;
import com.main.service.ProductService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/product-table")
public class ProductManagementRestController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // Hàm lấy danh mục lớn và trã về mãng các danh mục gồm [id, tên]
    @GetMapping("/categoriesParent")
    public ResponseEntity<List<CategoryMenuDTO>> getCategoriesParent() {
        try{
            if (!AuthUtil.getRole().equals("ROLE_ADMIN")) {
                throw new RuntimeException("Unauthorized access");
            }
            List<CategoryMenuDTO> list = new ArrayList();
            list = categoryService.getCategoryMenu();
            System.out.println("☎️😎😚👈");
            return ResponseEntity.ok().body(list);
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("🔥 Lỗi khi xử lý getCategoriesParent:");
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/proNew")
    public ResponseEntity<List<ProNewDTO>> getListProNew() {
        try{
            if (!AuthUtil.getRole().equals("ROLE_ADMIN")) {
                throw new RuntimeException("Unauthorized access");
            }
            List<ProNewDTO> list = new ArrayList();
            list = productService.getProNews();
            return ResponseEntity.ok().body(list);
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("🔥 Lỗi khi xử lý getCategoriesParent:");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/proNewBETA")
    public ResponseEntity<Map<String, Object>> getListProNewBETA() {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!AuthUtil.getRole().equals("ROLE_ADMIN")) {
                throw new RuntimeException("Unauthorized access");
            }

            // Lấy danh mục lớn
            List<CategoryDTO> list = categoryRepository.findAllRootCategories();

            // Sinh ID sản phẩm mới
            String newProductId = productService.generateProductId();

            // Gửi về client
            response.put("categories", list);
            response.put("newProductId", newProductId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("🔥 Lỗi khi xử lý getCategoriesParent:");
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateDTO dto) {
        try {
            // Kiểm tra dữ liệu
            if (dto.getName() == null || dto.getName().isBlank()
                    || dto.getBrand() == null || dto.getBrand().isBlank()
                    || dto.getCategoryName() == null || dto.getCategoryName().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Thiếu thông tin bắt buộc!"));
            }

            if (dto.getDescription() == null || dto.getDescription().trim().length() < 150) {
                return ResponseEntity.badRequest().body(Map.of("message", "Mô tả phải dài ít nhất 150 ký tự!"));
            }

            // Gọi service lưu
            productService.createProduct(dto);

            return ResponseEntity.ok(Map.of("message", "Tạo sản phẩm thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Có lỗi xảy ra khi tạo sản phẩm!"));
        }
    }


    // Hàm lấy sản phẩm theo page, tên, danh mục
    // Trã về: List Pro, Tổng page
    @PostMapping("/getListProduct")
    public ResponseEntity<Map<String, Object>> getTableProducts(@RequestBody Map<String, Object> body) {
        if (!AuthUtil.getRole().equals("ROLE_ADMIN")) {
            throw new RuntimeException("Unauthorized access");
        }
        String subCategory = (String) body.get("selectedSubCategory");
        String mainCategory = (String) body.get("selectedMainCategory");
        int pageSize = (Integer) body.get("pageSize");
        int currentPage = (Integer) body.get("currentPage");
        String searchName = (String) body.get("searchName");

        try{

            Map<String, Object> map = new HashMap();
            Page<ProductTableAdminDTO> page = productService.getPagedProducts(searchName, subCategory, mainCategory, currentPage, pageSize);
            map.put("list", page.getContent());
            map.put("totalPages", page.getTotalPages());
            System.out.println("😎😎😎😎😎😎😎😎😎😎😎✅22222🫦🫦🫦🫦🫦" + page.getTotalElements());
            return ResponseEntity.ok().body(map);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }





}
