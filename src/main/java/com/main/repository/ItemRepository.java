package com.main.repository;

import com.main.entity.Item;
import com.main.entity.Product;
import com.main.entity.Size;
import com.main.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByVariant(Variant variant);

    List<Item> findByVariant_Product_ProductID(String productID);

    @Query("""
        SELECT i.size FROM Item i
        WHERE i.variant.product.productID=:productID
""")
    List<Size> findSizeByVariant_Product_ProductID(@Param("productID") String productID);

    Item findByVariant_VariantIDAndSize_Code(String variantID, String sizeCode);

    void deleteByVariant(Variant variant);

    void deleteItemByVariant_Product(Product product);
}
