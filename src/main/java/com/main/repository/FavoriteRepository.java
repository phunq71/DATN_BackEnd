package com.main.repository;

import com.main.entity.Favorite;
import com.main.entity.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository  extends JpaRepository<Favorite, FavoriteId> {
}
