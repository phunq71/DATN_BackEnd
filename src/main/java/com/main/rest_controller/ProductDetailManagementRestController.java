package com.main.rest_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.dto.CategoryMenuDTO;
import com.main.dto.ProductDetailAdminDTO;
import com.main.dto.SizeDTO;
import com.main.dto.VariantSelectionDTO;
import com.main.entity.Image;
import com.main.entity.Product;
import com.main.entity.Size;
import com.main.entity.Variant;
import com.main.repository.*;
import com.main.service.ItemService;
import com.main.service.ProductService;
import com.main.service.SizeService;
import com.main.utils.AuthUtil;
import com.main.utils.FileUtil;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/productDetail")
public class ProductDetailManagementRestController {

    private final ProductService productService;
    private final SizeService sizeService;
    private final SizeRepository sizeRepository;
    private final ItemService itemService;
    private final VariantRepository variantRepository;
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final FileUtil fileUtil;

    @GetMapping("/getProduct")
    public ResponseEntity<ProductDetailAdminDTO> getProductDetail(@RequestParam String id) {

        try{
            System.out.println("😎🫦🫦🔎🔎🔎🔎🔎🔎🔎🔎🔎🔎🔎");
            if (!AuthUtil.getRole().equals("ROLE_ADMIN")) {
                throw new RuntimeException("Unauthorized access");
            }
            ProductDetailAdminDTO dto = productService.getProductDetail(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("🔥 Lỗi khi xử lý getCategoriesParent:");
            return ResponseEntity.badRequest().build();
        }

    }


    @PutMapping("/updateProduct")
    public ResponseEntity<Void> updateProductDetail(@RequestBody ProductDetailAdminDTO dto) {
        try {
            // Kiểm tra quyền truy cập
            if (!AuthUtil.getRole().equals("ROLE_ADMIN")) {

                System.err.println("Truy cập bị từ chối: người dùng không có quyền ADMIN.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
            }

            // Cập nhật sản phẩm
            productService.updateProductDetail(dto);

            // Thành công
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Ghi log lỗi server
            System.err.println("Lỗi khi cập nhật thông tin sản phẩm:");
            e.printStackTrace();

            // Trả về lỗi cho client
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }
    }

    @PostMapping("/update-size-config")
    public ResponseEntity<?> updateSizeConfig(@RequestBody ProductDetailAdminDTO productDTO) {
        try {
            if (!AuthUtil.getRole().equals("ROLE_ADMIN")) {

                System.err.println("Truy cập bị từ chối: người dùng không có quyền ADMIN.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
            }
            productService.updateVariantSizes(productDTO.getVariantSizes());
            System.out.println("❎❎❎❎❎❎❎❎❎❎❎❎❎❎");
            return ResponseEntity.ok("Cập nhật cấu hình size thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật cấu hình size: " + e.getMessage());
        }
    }


    // Chỉ lấy sizes thoi
    @GetMapping("/size")
    public ResponseEntity<List<Size>> getSizes() {
        List<Size> sizes = new ArrayList<>();
        sizes = sizeRepository.findAll();
        return new ResponseEntity<>(sizes, HttpStatus.OK);
    }


    // Thêm size mới
    @PostMapping("/submitSizeConfig")
    public ResponseEntity<Void> submitSizeConfig(@RequestBody Map<String, Object> data) {
        try {
            if (!AuthUtil.getRole().equals("ROLE_ADMIN")) {

                System.err.println("Truy cập bị từ chối: người dùng không có quyền ADMIN.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
            }
            Integer selectedSizeId = (Integer) data.get("selectedSizeId");
            List<String> selectedVariantIds = (List<String>) data.get("selectedVariantIds");

            System.out.println("😚😚😚😚😚😚😚😚😚" + selectedSizeId);
            itemService.addItem(selectedSizeId, selectedVariantIds);

            return ResponseEntity.ok().build();
        }catch (Exception e){
            if (!AuthUtil.getRole().equals("ROLE_ADMIN")) {

                System.err.println("Truy cập bị từ chối: người dùng không có quyền ADMIN.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
            }
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @Transactional
    @PostMapping("/updateVariant")
    public ResponseEntity<?> updateVariant(
            @RequestParam("selectedVariant") String selectedVariantJson,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "mainIndex", required = false) Integer mainIndex
    ) throws IOException, java.io.IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> variantMap = mapper.readValue(selectedVariantJson, new TypeReference<>() {});

        String id = (String) variantMap.get("id");
        Variant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found: " + id));

        // 1️⃣ Cập nhật field cơ bản
        variant.setColor((String) variantMap.get("color"));
        variant.setDescription((String) variantMap.get("description"));
        variant.setPrice(new BigDecimal(String.valueOf(variantMap.get("price"))));
        variant.setIsUse((Boolean) variantMap.get("isUse"));



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

        // 2️⃣ Lấy list fileName từ FE để xác định ảnh còn giữ
        List<Map<String, Object>> imagesJson = (List<Map<String, Object>>) variantMap.get("images");
        Set<String> jsonFileNames = imagesJson.stream()
                .filter(img -> img.get("fileName") != null)
                .map(img -> img.get("fileName").toString())
                .collect(Collectors.toSet());

        // 3️⃣ Xóa ảnh cũ không còn trong JSON
        List<Image> oldImages = imageRepository.findByVariant_VariantID(id);
        for (Image img : oldImages) {
            if (!jsonFileNames.contains(img.getImageUrl())) {
                fileUtil.deleteFile(img.getImageUrl());
                imageRepository.delete(img);
            }
        }

        // 4️⃣ Thêm ảnh mới
        if (files != null) {
            for (MultipartFile file : files) {
                String fileName = fileUtil.saveImage(file);

                Image img = new Image();
                img.setImageUrl(fileName);
                img.setIsMainImage(false);
                img.setVariant(variant);
                imageRepository.save(img);
            }
        }

        // 5️⃣ Đặt main image theo mainId (FE gửi id ảnh hoặc null)
        List<Image> allImages = imageRepository.findByVariant_VariantID(id);
        int i =0;
        for (Image img : allImages) {
            img.setIsMainImage(mainIndex != null && i == mainIndex);
            imageRepository.save(img);
            System.out.println("🙉 " + img.getImageUrl() + " => " + img.getIsMainImage());
            ++i;
        }

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        try{

            Product product = productRepository.findById(id).get();

            itemRepository.deleteItemByVariant_Product(product);

            imageRepository.deleteByVariant_Product(product);

            variantRepository.deleteByProduct(product);

            List<Image> images = product.getVariants()
                    .stream()
                    .flatMap(variant -> variant.getImages().stream())
                    .collect(Collectors.toList());

            productRepository.delete(product);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    images.forEach(img -> fileUtil.deleteFile(img.getImageUrl()));
                }
            });


            return ResponseEntity.ok().build();
        } catch (
                DataIntegrityViolationException e) {
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
