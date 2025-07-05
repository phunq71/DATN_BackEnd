package com.main.controller;
import com.main.dto.Image_DetailDTO;
import com.main.dto.Variant_DetailDTO;
import com.main.entity.*;
import com.main.repository.ItemRepository;
import com.main.repository.OrderDetailRepository;
import com.main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
public class ProductDetailController {

    private final ProductService productService;
    private final VariantService variantService;
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final ItemService itemService;

    public ProductDetailController(ProductService productService, VariantService variantService, CategoryService categoryService, ImageService imageService, ItemService itemService) {
        this.productService = productService;
        this.variantService = variantService;
        this.categoryService = categoryService;
        this.imageService = imageService;
        this.itemService = itemService;
    }

    // Sửa lại URL: thêm `/product` vào đầu
    @GetMapping("/opulentia/{categoryParent}/{categoryID}/{productId}/{variantID}")
    public String VariantDetail(Model model,
                                @PathVariable String productId,
                                @PathVariable String variantID) {
        Optional<Product> product = productService.findByProductID(productId);
        Category category = categoryService.findByCategoryId(product.get().getCategory().getCategoryId());
        List<Product> listProduct = productService.findByCategory(category);
        model.addAttribute("listProduct", listProduct);
        List<Variant_DetailDTO> listV = variantService.findByProduct(product.get());
        Optional<Variant> variant = variantService.findById(variantID);
        List<Image_DetailDTO> listImage = imageService.findByVariant(variant.get());
        List<Item> listI = itemService.findByVariant(variant.get());
        String formattedPrice = formatToVND(variant.get().getPrice());
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

