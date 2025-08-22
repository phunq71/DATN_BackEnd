package com.main.repository;


import com.main.dto.FacilityOrdManagerDTO;

import com.main.dto.AreaDTO;

import com.main.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityRepository  extends JpaRepository<Facility, String>  {
    List<Facility> getFacilityByIsUseTrue();



    @Query("""
        Select f.facilityId, f.facilityName
        , p.facilityId, p.facilityName
        from Facility f
        JOIN f.parent p
          where f.type = 'S'
        """)

    List<FacilityOrdManagerDTO> getShop();

    @Query("""
    SELECT f.facilityId, f.facilityName,
           p.facilityId, p.facilityName
    FROM Facility f
    JOIN f.parent p
     WHERE f.type = 'S'
         and p.type = 'Z'
      AND (:managerId IS NULL OR p.manager.staffID = :managerId)
    """)
    List<FacilityOrdManagerDTO> getShopByManager_ID(@Param("managerId") String managerId);

    @Query("""
    SELECT f.facilityId, f.facilityName,
           p.facilityId, p.facilityName
    FROM Facility f
    JOIN f.staffList s
    JOIN f.parent p
    WHERE f.type = 'S'
      AND p.type = 'Z'
      AND s.staffID = :staffId
    """)
    List<FacilityOrdManagerDTO> getShopByStaffID(@Param("staffId") String staffId);

    @Query("""
        SELECT f.facilityName
        FROM Facility f
        WHERE f.manager.staffID = :managerId
        """)
    String getFacilityNameByManagerId(@Param("managerId") String managerId);

    @Query("""
        SELECT f.facilityId
        FROM Staff s
        JOIN s.facility f
        WHERE s.staffID = :staffId
        """)
    String getFacilityIdByStaffId(@Param("staffId") String staffId);


    @Query("""
        SELECT f.facilityId
        FROM Facility f
        WHERE f.manager.staffID = :managerId
        """)
    String getFacilityIdByManagerId(@Param("managerId") String managerId);


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

    @Query("""
        SELECT p.facilityId
                , p.facilityName
                , f.facilityId
                , f.facilityName
                , p.manager.staffID
        FROM Facility f
        JOIN f.parent p
        WHERE f.type = 'S'
        AND  p.facilityId = :areaId
        ORDER BY p.facilityId
        """)
    List<AreaDTO> getAreaDtoByAreaId(@Param("areaId") String areaId);

    @Query("""
    SELECT p.facilityId
                , p.facilityName
                , f.facilityId
                , f.facilityName
                , p.manager.staffID
        FROM Facility f
        JOIN f.parent p
        WHERE f.type = 'W'
        ORDER BY p.facilityId
    """)
    List<AreaDTO> getAllAreasDTO();

    @Query("""
        SELECT grp.facilityId
                , grp.facilityName
                , f.facilityId
                , f.facilityName
                , grp.manager.staffID
        FROM Facility f
        JOIN f.parent p
        JOIN p.parent grp
        WHERE grp.facilityId = :areaId
        """)
    List<AreaDTO> getAllWarehousesOfShop(@Param("areaId") String areaId);

    @Query("""
        SELECT p.facilityId
                , p.facilityName
                , f.facilityId
                , f.facilityName
                , p.manager.staffID
        FROM Facility f
        JOIN f.parent p
        WHERE
        f.facilityId = :facilityId
        ORDER BY p.facilityId
        """)
    AreaDTO getAreaDtoByFacilityId(@Param("facilityId") String facilityId);

    //Kiểm tra có phải quản lý của cơ sở này hay không
    boolean existsByFacilityIdAndManager_StaffID(String facilityId, String managerId);

    //Kiểm tra có phải là cha con hay không
    boolean existsByFacilityIdAndParent_FacilityId(String facilityId, String parentFacilityId);

    Facility findByManager_StaffID(String staffID);


    @Query("""
        SELECT f.facilityId
        FROM Facility p
        JOIN Facility f on f.parent.facilityId = p.facilityId
        WHERE f.type = 'W'
            AND p.facilityId =:areaId
        """)
    String getWarehouseByAreaId(@Param("areaId") String areaId);

<<<<<<< HEAD
    // Lấy tất cả cơ sở con của một khu vực
    List<Facility> findByParent(Facility parent);
    boolean existsByTypeAndParent(String type, Facility parent);
    Optional<Facility> findTopByTypeOrderByFacilityIdDesc(String type);
=======
    @Query("""
        SELECT p.facilityId
        FROM Facility f
        JOIN f.parent p
        JOIN p.manager m
        WHERE f.facilityId = :shopId
        """)
    String getParentFacilityIdByShopId(@Param("shopId") String shopId);



>>>>>>> bc407da76dbe394066474505dee4ac519780ef88
}
