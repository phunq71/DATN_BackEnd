package com.main.serviceImpl;

import com.main.dto.*;
import com.main.entity.*;
import com.main.mapper.VariantMapper;
import com.main.repository.*;
import com.main.service.ProductService;
import com.main.utils.AuthUtil;
import com.main.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.main.mapper.ProductMapper;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final VariantRepository variantRepository;

    private final AccountRepository accountRepository;

    private final ReviewRepository reviewRepository;

    private final ProductMapper productMapper;

    private final FavoriteRepository favoriteRepository;
    private final ItemRepository itemRepository;
    private final SizeRepository sizeRepository;
    private final CategoryRepository categoryRepository;

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
        // Lấy danh sách product không có variant hoặc item
        List<Product> listTemp = productRepository.findProductsWithoutVariantsOrItems();
        Set<String> invalidProductIds = listTemp.stream()
                .map(Product::getProductID)
                .collect(Collectors.toSet());

        // Lọc ra các sản phẩm hợp lệ
        List<ProductViewDTO> dtoList = productViewDTOList.stream()
                .filter(p -> !invalidProductIds.contains(p.getProductID()))
                .collect(Collectors.toList());

        markFavorites(dtoList);
        return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());


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

        // Lấy danh sách product không có variant hoặc item
        List<Product> listTemp = productRepository.findProductsWithoutVariantsOrItems();
        Set<String> invalidProductIds = listTemp.stream()
                .map(Product::getProductID)
                .collect(Collectors.toSet());

        // Lọc ra các sản phẩm hợp lệ
        List<ProductViewDTO> dtoList = list.stream()
                .filter(p -> !invalidProductIds.contains(p.getProductID()))
                .collect(Collectors.toList());
        return dtoList;
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

        // Lấy danh sách product không có variant hoặc item
        List<Product> listTemp = productRepository.findProductsWithoutVariantsOrItems();
        Set<String> invalidProductIds = listTemp.stream()
                .map(Product::getProductID)
                .collect(Collectors.toSet());

        // Lọc ra các sản phẩm hợp lệ
        List<ProductViewDTO> dtoList = result.stream()
                .filter(p -> !invalidProductIds.contains(p.getProductID()))
                .collect(Collectors.toList());
        return dtoList;
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
        // Lấy danh sách product không có variant hoặc item
        List<Product> listTemp = productRepository.findProductsWithoutVariantsOrItems();
        Set<String> invalidProductIds = listTemp.stream()
                .map(Product::getProductID)
                .collect(Collectors.toSet());

        // Lọc ra các sản phẩm hợp lệ
        List<ProductViewDTO> dtoList = listDTO.stream()
                .filter(p -> !invalidProductIds.contains(p.getProductID()))
                .collect(Collectors.toList());
        return dtoList;
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
        // Lấy danh sách product không có variant hoặc item
        List<Product> listTemp = productRepository.findProductsWithoutVariantsOrItems();
        Set<String> invalidProductIds = listTemp.stream()
                .map(Product::getProductID)
                .collect(Collectors.toSet());

        // Lọc ra các sản phẩm hợp lệ
        List<ProductViewDTO> dtoList = dtos.stream()
                .filter(p -> !invalidProductIds.contains(p.getProductID()))
                .collect(Collectors.toList());
        markFavorites(dtoList);
        return dtoList;
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
        dtos.forEach(dto -> {
            System.out.println( "👉" + dto.getProductID());
        });

        // Lấy danh sách product không có variant hoặc item
        List<Product> listTemp = productRepository.findProductsWithoutVariantsOrItems();
        Set<String> invalidProductIds = listTemp.stream()
                .map(Product::getProductID)
                .collect(Collectors.toSet());

        // Lọc ra các sản phẩm hợp lệ
        List<ProductViewDTO> dtoList = dtos.stream()
                .filter(p -> !invalidProductIds.contains(p.getProductID()))
                .collect(Collectors.toList());
        return dtoList;
    }

    //3 trong 1
    @Override
    public Page<ProductViewDTO> searchAndFilterAllProducts(
            String keyword, String color, String brand,
            BigDecimal priceFrom, BigDecimal priceTo, Double minRating,
            String targetCustomer, String categoryId, String parentCategoryId,
            Pageable pageable, String sortType) {

        if (keyword != null) {
            keyword = keyword.trim();
        }

        Page<?> rawPage;
        List<Product> products;

        if ("price_asc".equalsIgnoreCase(sortType)) {
            rawPage = productRepository.searchAndFilterProductsPriceAsc(
                    keyword, color, brand, priceFrom, priceTo, minRating,
                    targetCustomer, categoryId, parentCategoryId, pageable
            );
            System.out.println("Sorting by price ASC");

            // Ép kiểu và lấy Product từ Object[]
            products = rawPage.getContent().stream()
                    .map(obj -> (Product) ((Object[]) obj)[0])
                    .toList();

        } else if ("price_desc".equalsIgnoreCase(sortType)) {
            rawPage = productRepository.searchAndFilterProductsPriceDesc(
                    keyword, color, brand, priceFrom, priceTo, minRating,
                    targetCustomer, categoryId, parentCategoryId, pageable
            );
            System.out.println("Sorting by price DESC");

            products = rawPage.getContent().stream()
                    .map(obj -> (Product) ((Object[]) obj)[0])
                    .toList();

        } else {
            Page<Product> defaultPage = productRepository.searchAndFilterProductsDefault(
                    keyword, color, brand, priceFrom, priceTo, minRating,
                    targetCustomer, categoryId, parentCategoryId, pageable
            );
            rawPage = defaultPage;
            products = defaultPage.getContent();
            System.out.println("Default sorting");
        }

        // In giá mainVariant để kiểm tra
        for (Product product : products) {
            product.getVariants().stream()
                    .filter(Variant::getIsMainVariant)
                    .findFirst()
                    .ifPresent(v -> System.out.println("Product ID: " + product.getProductID() + ", Price: " + v.getPrice()));
        }

        // Lấy danh sách bán chạy
        Pageable topSellingPageable = PageRequest.of(0, 10);
        List<Product> topSellingProducts = productRepository.findTopSellingProducts(topSellingPageable);
        List<String> hotProductIDs = topSellingProducts.stream()
                .map(Product::getProductID)
                .toList();

        // Map sang DTO
        List<ProductViewDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            ProductViewDTO dto = new ProductViewDTO();
            dto.setProductID(product.getProductID());

            boolean isNew = productRepository.isNewProduct(product.getProductID()) > 0;
            enrichProductViewDTO(dto, product, hotProductIDs, isNew);

            if (color != null) {
                product.getVariants().forEach(var -> {
                    if (var.getColor().equalsIgnoreCase(color)) {
                        dto.setVariantID(var.getVariantID());
                        for (Image img : var.getImages()) {
                            if (img.getIsMainImage()) {
                                dto.setImageUrl(img.getImageUrl());
                            }
                        }
                    }
                });
            }

            dtos.add(dto);
        }
        // Lấy danh sách product không có variant hoặc item
        List<Product> listTemp = productRepository.findProductsWithoutVariantsOrItems();
        Set<String> invalidProductIds = listTemp.stream()
                .map(Product::getProductID)
                .collect(Collectors.toSet());

        // Lọc ra các sản phẩm hợp lệ
        List<ProductViewDTO> dtoList = dtos.stream()
                .filter(p -> !invalidProductIds.contains(p.getProductID()))
                .collect(Collectors.toList());

        // Gắn yêu thích nếu có login
        markFavorites(dtoList);


        return new PageImpl<>(dtoList, pageable, rawPage.getTotalElements());
    }

    public Page<ProductTableAdminDTO> getPagedProducts(
            String keyword,
            String categoryId,
            String parentCategoryId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("productName").descending());
        Page<Product> products = productRepository.searchByNameAndCategory(keyword, categoryId, parentCategoryId, pageable);

        // Lấy danh sách product không có variant hoặc item
        List<Product> listTemp = productRepository.findProductsWithoutVariantsOrItems();
        Set<String> invalidProductIds = listTemp.stream()
                .map(Product::getProductID)
                .collect(Collectors.toSet());

        // Lọc ra các sản phẩm hợp lệ
        List<ProductTableAdminDTO> dtoList = products.getContent().stream()
                .filter(p -> !invalidProductIds.contains(p.getProductID()))
                .map(productMapper::toProductTableAdminDTO)
                .collect(Collectors.toList());

        // ⚡ Tổng phần tử đúng là tổng phần tử hợp lệ trong DB, không phải size trang hiện tại
        long totalValidProducts = products.getTotalElements() - invalidProductIds.size();

        return new PageImpl<>(dtoList, pageable, totalValidProducts);
    }



    public ProductDetailAdminDTO getProductDetail(String productID) {
        ProductDetailAdminDTO dto = new ProductDetailAdminDTO();
        Product product = productRepository.findById(productID).get();
        dto.setId(product.getProductID());
        dto.setName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setCreatedDate(product.getCreatedDate());
        dto.setTargetCustomer(product.getTargetCustomer());
        dto.setBrand(product.getBrand());
        dto.setCategoryName(product.getCategory().getCategoryName());

        List<Item> selectedItemsFromDB = itemRepository.findByVariant_Product_ProductID(product.getProductID());
        List<VariantSelectionDTO> variantSizes = new ArrayList<>();
        Set<String> selectedKeys = selectedItemsFromDB.stream()
                .map(item -> item.getVariant().getVariantID() + "-" + item.getSize().getCode())
                .collect(Collectors.toSet());

        for (Variant variant : product.getVariants()) {
            for (Size size : itemRepository.findSizeByVariant_Product_ProductID(product.getProductID())) {
                VariantSelectionDTO item = new VariantSelectionDTO();
                item.setVariantID(variant.getVariantID());   // hoặc getVariantId()
                item.setSizeCode(size.getCode());         // hoặc getSizeId()

                String key = variant.getVariantID() + "-" + size.getCode();
                item.setChecked(selectedKeys.contains(key));

                variantSizes.add(item);
            }
        }

        dto.setVariantSizes(variantSizes);

        List<VariantProDetailAdminDTO> list = new ArrayList<>();
        for (Variant variant : product.getVariants()) {
            VariantProDetailAdminDTO vaDto = new VariantProDetailAdminDTO();
            vaDto.setId(variant.getVariantID());
            vaDto.setColor(variant.getColor());
            vaDto.setDescription(variant.getDescription());
            vaDto.setCreatedDate(variant.getCreatedDate());
            vaDto.setPrice(variant.getPrice());
            Byte discountPercent = productRepository.findDiscountPercentByProductID(
                    dto.getId());
            if (discountPercent == null) {
                discountPercent = 0;
            }

            BigDecimal discountedPrice = vaDto.getPrice().subtract(
                    vaDto.getPrice().multiply(BigDecimal.valueOf(discountPercent).divide(BigDecimal.valueOf(100)))
            );

            vaDto.setDiscount(discountedPrice);

            vaDto.setIsUse(variant.getIsUse());
            vaDto.setIsMainVariant(variant.getIsMainVariant());

            List<ImageDTO> images = new ArrayList<>();
            variant.getImages().forEach(image -> {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(image.getImageId());
                imageDTO.setFileName(image.getImageUrl());
                imageDTO.setIsMain(image.getIsMainImage());
                images.add(imageDTO);
            });

            vaDto.setImages(images);
            list.add(vaDto);
        }

        dto.setListVariants(list);
        return dto;
    }

    @Override
    public void updateProductDetail(ProductDetailAdminDTO productDetail){
        try{
            Product product = productRepository.getById(productDetail.getId());
            product.setProductName(productDetail.getName());
            product.setDescription(productDetail.getDescription());
            product.setTargetCustomer(productDetail.getTargetCustomer());
            product.setBrand(productDetail.getBrand().toUpperCase());
            System.out.println("💾💾💾💾💾💾💾💾"+product.getTargetCustomer());
            productRepository.save(product);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateVariantSizes(List<VariantSelectionDTO> variantSizes) {
        for (VariantSelectionDTO dto : variantSizes) {
            // Tìm item theo variantID và sizeCode
            Item item = itemRepository
                    .findByVariant_VariantIDAndSize_Code(dto.getVariantID(), dto.getSizeCode());

            if (dto.isChecked()) {
                // Nếu người dùng check, mà item chưa có thì tạo mới
                if (item == null) {
                    Item newItem = new Item();
                    newItem.setVariant(variantRepository.findById(dto.getVariantID()).get()); // set variant theo ID
                    newItem.setSize(sizeRepository.findByCode(dto.getSizeCode()));        // set size theo ID
                    itemRepository.save(newItem);
                }
                // Nếu item đã tồn tại, không cần làm gì
            } else {
                // Nếu người dùng bỏ check, mà item đang tồn tại thì xoá
                if (item != null) {
                    itemRepository.delete(item);
                }
            }
        }
    }

    @Override
    public List<ProNewDTO> getProNews() {
        return productRepository.findProductsWithoutVariantsOrItems()
                .stream()
                .map(product -> new ProNewDTO(
                        product.getProductID(),
                        product.getProductName(),
                        product.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public String generateProductId() {
        String maxId = productRepository.findMaxProductId(); // VD: "ACC000000023"
        long nextId = 1;

        if (maxId != null && maxId.startsWith("Pro")) {
            try {
                long current = Long.parseLong(maxId.substring(3));
                nextId = current + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("AccountID không hợp lệ: " + maxId);
            }
        }

        return String.format("Pro%07d", nextId); // VD: ACC000000024
    }

    @Override
    public void createProduct(ProductCreateDTO dto) {
        Product product = new Product();
        product.setProductID(dto.getId());
        product.setProductName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setTargetCustomer(dto.getTargetCustomer());
        product.setBrand(dto.getBrand().toUpperCase());
        product.setCategory( categoryRepository.findByCategoryName (dto.getCategoryName()));
        product.setCreatedDate(dto.getCreatedDate());
        productRepository.save(product);
    }

}
