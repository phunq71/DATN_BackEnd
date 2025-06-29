package com.main.repository;

import com.main.entity.Inventory;
import com.main.entity.InventoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository  extends JpaRepository<Inventory, InventoryId> {
}
