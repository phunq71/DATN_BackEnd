package com.main.repository;

import com.main.entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetunRequestRepository extends JpaRepository<ReturnRequest, Integer > {
}
