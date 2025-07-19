package com.main.repository;

import com.main.entity.InventorySlip;
import jakarta.persistence.OneToMany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventorySlipRepository  extends JpaRepository<InventorySlip, String > {
}
