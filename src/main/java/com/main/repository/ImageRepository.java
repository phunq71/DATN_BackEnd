package com.main.repository;

import com.main.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository  extends JpaRepository<Image, Integer> {
    @Query("SELECT i.imageUrl FROM Image i ORDER BY i.imageUrl ASC")
    public List<String> getImage();
}
