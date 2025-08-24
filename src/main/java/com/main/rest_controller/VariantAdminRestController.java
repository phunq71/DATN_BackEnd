package com.main.rest_controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.entity.Image;
import com.main.entity.Variant;
import com.main.mapper.VariantMapper;
import com.main.repository.ImageRepository;
import com.main.repository.ItemRepository;
import com.main.repository.ProductRepository;
import com.main.repository.VariantRepository;
import com.main.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/variant")
public class VariantAdminRestController {

    private final VariantRepository variantRepository;
    private final ImageRepository imageRepository;
    private final VariantMapper variantMapper;
    private final ProductRepository productRepository;
    private final ItemRepository itemRepository;

    @PostMapping("/add")
    public ResponseEntity<?> createVariant(
            @RequestParam("selectedVariant") String selectedVariantJson,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        // Parse JSON sang Map
        Map<String, Object> variantMap = mapper.readValue(selectedVariantJson, new TypeReference<>() {});

        // Lấy danh sách images (nếu có)
        List<Map<String, Object>> imagesJson = (List<Map<String, Object>>) variantMap.get("images");
        System.out.println("Images JSON: " + imagesJson);
        // Lấy productId từ JSON
        String productId = (String) variantMap.get("productId");
        // --- Phần còn lại tạo Variant & lưu file ---
        Variant variant = new Variant();
        variant.setProduct(productRepository.findById(productId).get());
        variant.setVariantID((String) variantMap.get("id"));
        variant.setColor((String) variantMap.get("color"));
        variant.setDescription((String) variantMap.get("description"));
        variant.setPrice(new BigDecimal(String.valueOf(variantMap.get("price"))));
        variant.setIsMainVariant(Boolean.TRUE.equals(variantMap.get("isMainVariant")));
        variant.setIsUse(Boolean.TRUE.equals(variantMap.get("isUse")));
        variant.setCreatedDate(LocalDate.now());
        Boolean isMainVariant = (Boolean) variantMap.get("isMainVariant");

        if (Boolean.TRUE.equals(isMainVariant)) {
            Variant oldMain = variantRepository.findMainVariantsByProductID(variant.getProduct().getProductID());
            if (oldMain != null && !oldMain.getVariantID().equals(variant.getVariantID())) {
                oldMain.setIsMainVariant(false);
                variantRepository.save(oldMain);
            }
        }
        variant.setIsMainVariant(isMainVariant);
        variantRepository.save(variant);

        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);

                // Lưu file
                String fileName = FileUtil.saveImage(file);

                // Lấy isMain theo index từ imagesJson
                boolean isMain = false;
                if (imagesJson != null && imagesJson.size() > i) {
                    Object isMainObj = imagesJson.get(i).get("isMain");
                    isMain = isMainObj != null && (Boolean) isMainObj;
                }

                Image img = new Image();
                img.setImageUrl(fileName);
                img.setIsMainImage(isMain);
                img.setVariant(variant);

                imageRepository.save(img);
            }
        }

        return ResponseEntity.ok("Variant created successfully!");
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVariant(@PathVariable("id") String id) {
        try {
            System.out.println("Delete Variant : " + id);
            if (!variantRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy variant với ID: " + id);
            }


             Variant variant =  variantRepository.findById(id).get();

            if(variant.getIsMainVariant()) {
                return ResponseEntity.ok("Không thể xóa biến thể vì là bến thể chính!");
            }

            if(productRepository.findById(variant.getProduct().getProductID()).get().getVariants().size() == 1){
                return ResponseEntity.ok("Không thể xóa biến thể vì sản phẩm cần ít nhất 1 biến thể! ");
            }

            System.out.println("‼️");
            itemRepository.deleteByVariant(variant);
            System.out.println("‼️‼️");
            imageRepository.deleteByVariant(variant);
            System.out.println("‼️‼️‼️");
            variantRepository.delete(variant);
            System.out.println("‼️‼️‼️‼️️");
            List<Image> images = new ArrayList<>(variant.getImages());

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    images.forEach(img -> FileUtil.deleteFile(img.getImageUrl()));
                }
            });
            System.out.println("‼️‼️‼️‼️‼️️");
            return ResponseEntity.ok("Xóa thành công variant " + id);

        } catch (DataIntegrityViolationException e) {
            // Lỗi ràng buộc FK (ví dụ vẫn còn ảnh hoặc order tham chiếu)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Không thể xóa vì variant đang được tham chiếu!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa variant: " + e.getMessage());
        }
    }



}
