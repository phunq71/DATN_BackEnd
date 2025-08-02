package com.main.repository;

import com.main.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository  extends JpaRepository<Facility, String>  {
    List<Facility> getFacilityByIsUseTrue();

    @Query("""
        SELECT f.facilityName
        FROM Facility f
        WHERE f.manager.staffID = :managerId
        """)
    String getFacilityNameByManagerId(@Param("managerId") String managerId);
}
