package com.main.repository;

import com.main.dto.AreaDTO;
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

    @Query("""
        SELECT p.facilityId
                , p.facilityName
                , f.facilityId
                , f.facilityName
                , p.manager.staffID
        FROM Facility f
        JOIN f.parent p
        WHERE f.type = 'S'
        AND (:managerId is null OR p.manager.staffID = :managerId)
        ORDER BY p.facilityId
        """)
    List<AreaDTO> getArea(@Param("managerId") String managerId);

    boolean existsByFacilityIdAndManager_StaffID(String facilityId, String managerId);
    Facility findByManager_StaffID(String staffID);


    @Query("""
        SELECT f.facilityId
        FROM Facility p
        JOIN Facility f on f.parent.facilityId = p.facilityId
        WHERE f.type = 'W'
            AND p.facilityId =:areaId
        """)
    String getWarehouseByAreaId(@Param("areaId") String areaId);
}
