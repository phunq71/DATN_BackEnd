package com.main.repository;

import com.main.entity.Image;
import com.main.entity.Product;
import com.main.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository  extends JpaRepository<Image, Integer> {
    List<Image> findByVariant(Variant variant);
    @Query("SELECT i.imageUrl FROM Image i ORDER BY i.imageUrl ASC")
    public List<String> getImage();

    List<Image> findByVariant_VariantID(String id);

    void deleteByVariant(Variant variant);

    void deleteByVariant_Product(Product product);
}
