package com.main.repository;

import com.main.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SỉzeRepository extends JpaRepository<Size, Integer> {
}
