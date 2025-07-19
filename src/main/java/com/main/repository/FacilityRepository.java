package com.main.repository;

import com.main.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository  extends JpaRepository<Facility, String>  {
}
