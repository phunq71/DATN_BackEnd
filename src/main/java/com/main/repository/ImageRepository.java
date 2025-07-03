package com.main.repository;

import com.main.entity.Image;
import com.main.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository  extends JpaRepository<Image, Integer> {
    List<Image> findByVariant(Variant variant);
}
