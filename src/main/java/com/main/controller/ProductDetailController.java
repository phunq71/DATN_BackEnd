package com.main.controller;
import com.main.dto.Image_DetailDTO;
import com.main.dto.Variant_DetailDTO;
import com.main.entity.*;
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
    @Autowired
    VariantService variantService;
    @Autowired
    ProductService productService;
    @Autowired
    ImageService imageService;
    @Autowired
    ItemService itemService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @GetMapping("/opulentia")
    public String ProductDetailPAGE(Model model) {
        return "View/productDetail";
    }
    //Yêu cầu /opulentia/{categoryParent}/{categoryID}/{productId} <--id sản phẩm>>.
    @GetMapping("/opulentia/{categoryParent}/{categoryID}/{productId}")
    public String ProductDetail(Model model,
                                @PathVariable String productId) {
        Optional<Product> product = productService.findByProductID(productId);
        List<Variant_DetailDTO> listV = variantService.findByProduct(product.get());
        model.addAttribute("product", product.get());
        model.addAttribute("listV", listV);
        return "View/productDetail";
    }
    // Yêu cầu /opulentia/<<danh mục cha>>/ <<danh mục con>>/ ?idPro = <<id sản phẩm>>/ ?idVar =”id biến thể
    // Mẫu http://localhost:8989/opulentia/c1/c2/Pro0000011/pro0000011-02
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
        model.addAttribute("listI", listI);
        model.addAttribute("descriptionVariant", variant.get().getDescription());
        model.addAttribute("colorVariant", variant.get().getColor());
        String formattedPrice = formatToVND(variant.get().getPrice());
        model.addAttribute("priceVariant", formattedPrice);
        model.addAttribute("listImage", listImage);
        model.addAttribute("product", product.get());
        model.addAttribute("listV", listV);
        return "View/productDetail";
    }
    //Hàm format về tiền VNĐ
    private String formatToVND(Number amount) {
        if (amount == null) return "0 VNĐ";
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + " VNĐ";
    }
}
