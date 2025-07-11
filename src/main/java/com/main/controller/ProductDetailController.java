package com.main.controller;
import com.main.dto.Image_DetailDTO;
import com.main.dto.Variant_DetailDTO;
import com.main.entity.*;
import com.main.service.*;
import com.main.serviceImpl.InventoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.text.NumberFormat;
import java.util.*;

@Controller
public class ProductDetailController {

    private final ProductService productService;
    private final VariantService variantService;
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final ItemService itemService;
    private final InventoryService inventoryService;
    private final ReviewService reviewService;
    public ProductDetailController(ProductService productService, VariantService variantService, CategoryService categoryService, ImageService imageService, ItemService itemService, InventoryService inventoryService, ReviewService reviewService) {
        this.productService = productService;
        this.variantService = variantService;
        this.categoryService = categoryService;
        this.imageService = imageService;
        this.itemService = itemService;
        this.inventoryService = inventoryService;
        this.reviewService = reviewService;
    }

    // Sửa lại URL: thêm `/product` vào đầu
    @GetMapping("/opulentia/{categoryParent}/{categoryID}/{productId}/{variantID}")
    public String VariantDetail(Model model,
                                @PathVariable String productId,
                                @PathVariable String variantID) {
        Optional<Product> product = productService.findByProductID(productId);
        //DS CUNG LOAI
        Category category = categoryService.findByCategoryId(product.get().getCategory().getCategoryId());
        List<Product> listProduct = productService.findByCategory(category);
        model.addAttribute("listProduct", listProduct);
        //Image slider, màu sắc
        List<Variant_DetailDTO> listV = variantService.findByProduct(product.get());
        Optional<Variant> variant = variantService.findById(variantID);
        //Tìm ds ảnh bằng bằng biến thể
        List<Image_DetailDTO> listImage = imageService.findByVariant(variant.get());
        //Tìm ds tìm item bằng biến thể để lấy size của biến thể
        List<Item> listI = itemService.findByVariant(variant.get());
        String formattedPrice = formatToVND(variant.get().getPrice());

        // Lấy số lượng đánh giá theo từng mức sao
        Map<Integer, Integer> ratingCounts = reviewService.getReviewRatingCounts(productId);
        // Đếm tổng số lượt đánh giá cho sản phẩm
        Integer sumRating = reviewService.countReviewsByProductID(productId);
        // Khởi tạo giá trị mặc định cho điểm trung bình và phần trăm tổng thể
        double averageRating = 0.0;
        double overallPercentage = 0.0;
        // Map để lưu trữ phần trăm của từng mức sao
        Map<Integer, Double> ratingPercentages = new HashMap<>();
        // Nếu có đánh giá (tức là tổng số đánh giá > 0)
        if (sumRating != null && sumRating > 0) {
            // Tính điểm đánh giá trung bình
            averageRating = reviewService.findAverageRatingByProductId(productId);
            // Tính phần trăm cho từng mức sao dựa trên tổng số đánh giá
            for (Map.Entry<Integer, Integer> entry : ratingCounts.entrySet()) {
                double percentage = (entry.getValue() * 100.0) / sumRating;
                ratingPercentages.put(entry.getKey(), percentage);
            }
            // Tính phần trăm tổng thể dựa trên điểm trung bình (thang điểm 5)
            overallPercentage = (averageRating / 5.0) * 100;
        } else {
            sumRating = 0;
        }
        model.addAttribute("sumRating", sumRating);
        model.addAttribute("ratingCounts", ratingCounts);
        model.addAttribute("ratingPercentages", ratingPercentages);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("overallPercentage", overallPercentage);
        model.addAttribute("variantID", variantID);
        model.addAttribute("variants", variant.get());
        model.addAttribute("listI", listI);
        model.addAttribute("descriptionVariant", variant.get().getDescription());
        model.addAttribute("colorVariant", variant.get().getColor());
        model.addAttribute("priceVariant", formattedPrice);
        model.addAttribute("listImage", listImage);
        model.addAttribute("product", product.get());
        model.addAttribute("listV", listV);
        return "View/productDetail";
    }
    private String formatToVND(Number amount) {
        if (amount == null) return "0 VNĐ";
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + " VNĐ";
    }
}

