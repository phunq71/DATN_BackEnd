package com.main.serviceImpl;

import com.main.dto.CategoryDTO;
import com.main.entity.Category;
import com.main.dto.CategoryMenuDTO;
import com.main.entity.Variant;
import com.main.mapper.CategoryMapper;
import com.main.repository.CategoryFlatResult;
import com.main.repository.CategoryRepository;
import com.main.service.CategoryService;
import com.main.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;


    @Override
    public Category findByCategoryId(String categoryId) {
        return categoryRepository.findByCategoryId(categoryId);

    }

    //tìm kiếm danh mục theo id
    @Override
    public String findNameById(String id) {
        return categoryRepository.findById(id)
                .map(Category::getCategoryName)
                .orElse(null);
    }

    @Override
    public List<CategoryMenuDTO> getCategoryMenu() {
        List<CategoryFlatResult> flatResults = categoryRepository.fetchCategoryWithChildren();
        return CategoryMapper.toCategoryMenuDTO(flatResults);
    }

    @Override
    public List<CategoryDTO> getAllCategoriesAsTree() {
        List<Category> categories = categoryRepository.findAll();

        // Map sang DTO
        Map<String, CategoryDTO> dtoMap = new HashMap<>();
        List<CategoryDTO> roots = new ArrayList<>();

        for (Category entity : categories) {
            CategoryDTO dto = toDTO(entity);
            dtoMap.put(dto.getCategoryID(), dto);
        }

        for (Category entity : categories) {
            CategoryDTO dto = dtoMap.get(entity.getCategoryId());
            if (entity.getParent() != null) {
                String parentId = entity.getParent().getCategoryId();
                CategoryDTO parentDto = dtoMap.get(parentId);
                if (parentDto.getChildren() == null) {
                    parentDto.setChildren(new ArrayList<>());
                }
                parentDto.getChildren().add(dto);
            } else {
                roots.add(dto); // danh mục cha (không có parent)
            }
        }

        return roots;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO dto, MultipartFile file){
        Category entity = new Category();
        entity.setCategoryName(dto.getCategoryName());

        String prefix;
        if (dto.getParentID() != null && !dto.getParentID().isEmpty()) {
            Category parent = categoryRepository.findById(dto.getParentID())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha"));
            entity.setParent(parent);
            prefix = getFirstCharWithoutAccent(parent.getCategoryName()).toUpperCase();
        } else {
            prefix = getFirstCharWithoutAccent(dto.getCategoryName()).toUpperCase();
        }

        entity.setCategoryId(generateCategoryID(prefix));
        entity.setCategoryName(dto.getCategoryName());
        String nameFile = null;
        if(file != null){
            try {
                nameFile = FileUtil.saveImage(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            entity.setBanner(nameFile);
        }
        entity.setContent(dto.getContent());
        categoryRepository.save(entity);
        return toDTO(entity);
    }

    @Override
    public CategoryDTO updateCategory(String id, CategoryDTO dto, MultipartFile file){
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        entity.setCategoryName(dto.getCategoryName());
        if (dto.getParentID() != null) {
            entity.setParent(categoryRepository.findById(dto.getParentID()).orElse(null));
        } else {
            entity.setParent(null);
        }


        String nameFile = null;
        if(file != null){
            try {
                nameFile = FileUtil.saveImage(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FileUtil.deleteFile(dto.getBanner());
            entity.setBanner(nameFile);
        }
        entity.setContent(dto.getContent());
        categoryRepository.save(entity);
        return toDTO(entity);
    }

    @Override
    public void deleteCategory(String id) {
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        boolean hasActiveProduct = entity.getProducts().stream()
                .flatMap(p -> p.getVariants().stream())
                .anyMatch(Variant::getIsUse);

        if (hasActiveProduct) {
            throw new RuntimeException("Không thể xóa danh mục vì có sản phẩm đang hoạt động.");
        }
        FileUtil.deleteFile(entity.getBanner());
        categoryRepository.delete(entity);

    }

    @Override
    public List<CategoryDTO> searchByName(String keyword) {
        List<Category> list = categoryRepository.findByCategoryNameContainingIgnoreCase(keyword);
        return list.stream().map(this::toDTO).toList();
    }

    private CategoryDTO toDTO(Category entity) {
        return new CategoryDTO(
                entity.getCategoryId(),
                entity.getCategoryName(),
                entity.getParent() != null ? entity.getParent().getCategoryId() : null,
                entity.getContent(),
                entity.getBanner(),
                new ArrayList<>()
        );
    }

    private String generateCategoryID(String prefix) {
        // Tìm tất cả ID có prefix (ví dụ: "A")
        List<Category> categoriesWithPrefix = categoryRepository.findByCategoryIdStartingWith(prefix);

        int max = categoriesWithPrefix.stream()
                .mapToInt(c -> Integer.parseInt(c.getCategoryId().substring(prefix.length())))
                .max()
                .orElse(0);

        return prefix + String.format("%04d", max + 1);
    }

    private String getFirstCharWithoutAccent(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").substring(0, 1);
    }


}
