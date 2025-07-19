package com.main.repository;

import com.main.entity.InventorySlipDetail;
import com.main.entity.InventorySlipDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventorySlipDetailRepository  extends JpaRepository<InventorySlipDetail, InventorySlipDetailId> {
}
