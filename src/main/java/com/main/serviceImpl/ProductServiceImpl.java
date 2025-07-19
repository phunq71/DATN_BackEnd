package com.main.serviceImpl;

import com.main.dto.ColorOption;
import com.main.dto.ProductViewDTO;
import com.main.dto.SupportDetailDTO;
import com.main.entity.*;
import com.main.mapper.VariantMapper;
import com.main.repository.*;
import com.main.service.ProductService;
import com.main.utils.AuthUtil;
import com.main.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.main.dto.ProductByCategoryDTO;
import com.main.mapper.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final VariantRepository variantRepository;

    private final AccountRepository accountRepository;

    private final ReviewRepository reviewRepository;

    private final ProductMapper productMapper;

    private final FavoriteRepository favoriteRepository;

    //Tim sp cho trang chi tiet sp
    @Override
    public Optional<Product> findByProductID(String productID){
        return productRepository.findById(productID);
    }
    @Override
    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }
    //hiển thị sp theo dm có phân trang
    @Override
    public Page<ProductViewDTO> getProductsByCategory(String parentId, String childId, int page) {
        Pageable pageable = PageRequest.of(page, 12); // 12 sản phẩm/trang
        Page<Product> productPage;
        if (childId == null || childId.equals("null")) {
            productPage = productRepository.findByParentCategoryOnly(parentId, pageable);//lọc theo dm cha
        } else {
            productPage = productRepository.findByParentAndChildCategory(parentId, childId, pageable);
        }
        //chuyển đổi Page<p> thành page<PbyC>
        List<ProductViewDTO> productViewDTOList = new ArrayList<>();
        List<ProductByCategoryDTO> list = productPage.map(ProductMapper::toDTO).getContent();
        Pageable pageable1 = PageRequest.of(0, 10);
        List<Product> products = productRepository.findTopSellingProducts(pageable1);
        List<String> hotProductIDs = products.stream().map(Product::getProductID).toList();
        list.forEach(pro ->{
            ProductViewDTO productViewDTO = new ProductViewDTO();
            Product product = productRepository.findById(pro.getProductID()).orElse(null);
            productViewDTO.setProductID(pro.getProductID());
            enrichProductViewDTO(productViewDTO, product, hotProductIDs,productRepository.isNewProduct(product.getProductID()) > 0);

            productViewDTOList.add(productViewDTO);
        });
        markFavorites(productViewDTOList);
        return new PageImpl<>(productViewDTOList, pageable, productPage.getTotalElements());

    }

    @Override
    public List<ProductViewDTO> findProductsSale() {
        List<ProductViewDTO> list = new ArrayList<>();
        list = productRepository.findDiscountedProducts();
        if (!list.isEmpty()) {
            //set isFavorite
            markFavorites(list);
            // set isNew và hot
            Pageable pageable = PageRequest.of(0, 10);
            List <Product> products = productRepository.findTopSellingProducts(pageable);
            List<String> productIDs = products.stream().map(Product::getProductID).toList();
            list.forEach(product -> {
                product.setIsNew(productRepository.isNewProduct(product.getProductID()) > 0);
                Product pro = productRepository.getReferenceById(product.getProductID());
                enrichProductViewDTO(product, pro, productIDs, product.getIsNew());
            });

        }
        return list;
    }

    @Override
    public List<ProductViewDTO> findProductNews() {
        List<ProductViewDTO> result = new ArrayList<>();
        List<Product> listPro = productRepository.findRecentProducts();

        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = productRepository.findTopSellingProducts(pageable);
        List<String> hotProductIDs = products.stream().map(Product::getProductID).toList();

        for (Product product : listPro) {
            ProductViewDTO dto = new ProductViewDTO();
            dto.setProductID(product.getProductID());
            enrichProductViewDTO(dto, product, hotProductIDs, true);
            result.add(dto);
        }

        List<Variant> variantList = variantRepository.findNewVariantsOfOldProducts();
        variantList.forEach(variant -> {
            ProductViewDTO dto = new ProductViewDTO();
            Product product = variant.getProduct();
            enrichProductViewDTO(dto, product, hotProductIDs, true);
            variant.getImages().forEach(img -> {
                if(img.getIsMainImage()){
                    dto.setProductID(product.getProductID());
                    dto.setImageUrl(img.getImageUrl());
                }
            });
            dto.setVariantID(variant.getVariantID());
            result.add(dto);
        });
        return result;
    }
    @Override
    public List<ProductViewDTO> findBestSellingProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        List <Product> products = productRepository.findTopSellingProducts(pageable);
        List<String> hotProductIDs = products.stream().map(Product::getProductID).toList();
        List<ProductViewDTO> listDTO = new ArrayList<>();
        products.forEach(product -> {
            ProductViewDTO dto = new ProductViewDTO();
            dto.setProductID(product.getProductID());
            enrichProductViewDTO(dto, product, hotProductIDs, productRepository.isNewProduct(product.getProductID()) > 0);
            listDTO.add(dto);
        });
        return listDTO;
    }


    private Double calculateAvgRating(String productID) {
        Double avg = reviewRepository.findAverageRatingByProductId(productID);
        if (avg != null) {
            BigDecimal rounded = new BigDecimal(avg).setScale(1, RoundingMode.HALF_UP);
            return rounded.doubleValue();
        }
        return null;
    }

    private List<ColorOption> extractColorOptions(List<Variant> variants) {
        List<ColorOption> colorList = DataUtils.getListColors();

        return variants.stream()
                .filter(Variant::getIsUse) // Lọc các variant đang hoạt động
                .map(variant -> {
                    String colorName = variant.getColor();
                    return colorList.stream()
                            .filter(color -> color.getTen().equalsIgnoreCase(colorName))
                            .findFirst()
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }



    private void enrichProductViewDTO(ProductViewDTO dto, Product product, List<String> hotProductIDs, boolean isNew) {
        ProductByCategoryDTO mapped = ProductMapper.toDTO(product);
        dto.setProductName(mapped.getProductName());
        dto.setPrice(mapped.getPrice());
        dto.setParentCategoryId(product.getCategory().getParent().getCategoryId());
        dto.setChildCategoryId(product.getCategory().getCategoryId());
        dto.setImageUrl(mapped.getMainImageUrl());
        dto.setVariantID(mapped.getVariantMainID());
        dto.setDiscountPercent(productRepository.findDiscountPercentByProductID(product.getProductID()));
        dto.setIsHot(hotProductIDs.contains(product.getProductID()));
        dto.setIsNew(isNew);
        dto.setAvg_rating(calculateAvgRating(product.getProductID()));
        dto.setOptions(extractColorOptions(product.getVariants()));
        // bổ sung thuộc tính số totalLikes
        dto.setTotalLikes(favoriteRepository.countFavoriteByProduct_ProductID(product.getProductID()));
    }

    @Override
    public void markFavorites(List<ProductViewDTO> products) {
        String accountID = AuthUtil.getAccountID();
        if (accountID == null) return;

        Optional<Account> optionalAccount = accountRepository.findByAccountId(accountID);
        if (optionalAccount.isEmpty()) return;

        List<Favorite> favorites = optionalAccount.get().getCustomer().getFavorites();
        List<String> favoriteProductIDs = favorites.stream()
                .map(fav -> fav.getProduct().getProductID())
                .toList();

        products.forEach(product ->
                product.setIsFavorite(favoriteProductIDs.contains(product.getProductID()))
        );
    }

    @Override
    public SupportDetailDTO getSupportDetail(String variantId) {
        Variant variant = variantRepository.findById(variantId).get();
        SupportDetailDTO dto = new SupportDetailDTO();

        String accountID = AuthUtil.getAccountID();
        if (accountID == null) dto.setIsFavorite(false);
        else{
            Optional<Account> optionalAccount = accountRepository.findByAccountId(accountID);
            if (optionalAccount.isEmpty()) dto.setIsFavorite(false);
            else{
                List<Favorite> favorites = optionalAccount.get().getCustomer().getFavorites();
                List<String> favoriteProductIDs = favorites.stream()
                        .map(fav -> fav.getProduct().getProductID())
                        .toList();
                dto.setIsFavorite(favoriteProductIDs.contains(variant.getProduct().getProductID()));
            }
        }
        dto.setDiscountPercent(productRepository.findDiscountPercentByProductID(variant.getProduct().getProductID()));
        dto.setIsNew(variantRepository.isNewVariantOf(variantId)>0);

        Pageable pageable = PageRequest.of(0, 10);
        List <Product> products = productRepository.findTopSellingProducts(pageable);
        List<String> hotProductIDs = products.stream().map(Product::getProductID).toList();

        dto.setIsHot(hotProductIDs.contains(variant.getProduct().getProductID()));
        dto.setSoldQuantity(productRepository.countSoldQuantityByProductId(variant.getProduct().getProductID()));
        dto.setFavorites(favoriteRepository.countFavoriteByProduct_ProductID(variant.getProduct().getProductID()));
        return dto;
    }
    public List<Product> findAll(){
        return productRepository.findAll();
    }
    @Override
    public Page<ProductViewDTO> filterProductsWithReviewOnly(
            String color,
            String brand,
            BigDecimal priceFrom,
            BigDecimal priceTo,
            Double minRating,
            String targetCustomer,
            String categoryId,
            String parentCategoryId,
            Pageable pageable
    ) {
           Page<Product> pagePro = productRepository.filterProductsWithReviewOnly(
                color,
                brand,
                priceFrom,
                priceTo,
                minRating,
                targetCustomer,
                categoryId,
                parentCategoryId,
                pageable
        );
           List<Product> products = pagePro.getContent();
           System.err.println("Sizeeeeeeeeeeeee: " + products.size());
           List<ProductViewDTO> dtos = new ArrayList<>();
           Pageable pageable1 = PageRequest.of(0, 10);
           List<Product> productss = productRepository.findTopSellingProducts(pageable1);
           List<String> hotProductIDs = productss.stream().map(Product::getProductID).toList();
           products.forEach(product -> {
               ProductViewDTO dto = new ProductViewDTO();
               dto.setProductID(product.getProductID());
               enrichProductViewDTO(dto, product, hotProductIDs, productRepository.isNewProduct(product.getProductID())>0);
               Variant variant = new Variant();
               if (color!=null){
                   product.getVariants().forEach(var -> {
                       if (var.getColor().equalsIgnoreCase(color)){
                           dto.setVariantID(var.getVariantID());
                            for (Image img  : var.getImages()){
                                if(img.getIsMainImage()) {
                                    dto.setImageUrl(img.getImageUrl());
                                }
                            }
                       }
                   });
               }
                dtos.add(dto);
           });
        markFavorites(dtos);
        return new PageImpl<>(dtos, pageable, pagePro.getTotalElements());
    }

    @Override
    public List<ProductViewDTO> findTopFavorited() {
        Pageable pageable1 = PageRequest.of(0, 10);
        List<Product> pros = productRepository.findTopSellingProducts(pageable1);
        List<String> hotProductIDs = pros.stream().map(Product::getProductID).toList();

        List<Product> products = productRepository.findTop12RankedProducts();
        List<ProductViewDTO> dtos = new ArrayList<>();
        products.forEach(product -> {
            ProductViewDTO dto = new ProductViewDTO();
            dto.setProductID(product.getProductID());
            enrichProductViewDTO(dto, product, hotProductIDs, productRepository.isNewProduct(dto.getProductID()) > 0);
            dtos.add(dto);
        });
        // 4. Đánh dấu sản phẩm yêu thích (nếu có login)
        markFavorites(dtos);
        return dtos;

    }

    @Override
    public List<ProductViewDTO> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isBlank()) {
            return new ArrayList<>();
        }

        // Cắt bỏ khoảng trắng đầu/cuối
        keyword = keyword.trim();

        // 1. Tìm các sản phẩm tên khớp với keyword
        List<Product> products = productRepository.searchProductsByName(keyword);

        // 2. Lấy danh sách sản phẩm bán chạy nhất (top 10)
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> topSellingProducts = productRepository.findTopSellingProducts(pageable);
        List<String> hotProductIDs = topSellingProducts.stream()
                .map(Product::getProductID)
                .toList();

        // 3. Map thành ProductViewDTO
        List<ProductViewDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            ProductViewDTO dto = new ProductViewDTO();
            dto.setProductID(product.getProductID());
            boolean isNew = productRepository.isNewProduct(product.getProductID()) > 0;
            enrichProductViewDTO(dto, product, hotProductIDs, isNew);
            dtos.add(dto);
        }

        // 4. Đánh dấu sản phẩm yêu thích (nếu có login)
        markFavorites(dtos);
        return dtos;
    }
}
