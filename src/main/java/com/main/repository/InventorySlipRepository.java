package com.main.repository;

import com.main.dto.InvSlipDTO;
import com.main.dto.InvSlipDetailDTO;
import com.main.dto.ItemInvSlipDTO;
import com.main.entity.InventorySlip;
import jakarta.persistence.OneToMany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventorySlipRepository  extends JpaRepository<InventorySlip, String > {

    @Query("""
        SELECT inv.isid
            ,  inv.type
            , ff.facilityId
            , ff.facilityName
            , tf.facilityId
            , tf.facilityName
            , inv.status
            , s.staffID
            , s.fullname
            , apv.staffID
            , apv.fullname
            , inv.createDate
            , inv.note
        FROM InventorySlip inv
        JOIN inv.staff s
        LEFT JOIN inv.approver apv
        LEFT JOIN inv.toFacility tf
        LEFT JOIN inv.fromFacility ff
               WHERE inv.createDate >= :startOfDay
                 AND inv.createDate < :endOfDay
        AND (:type is null OR inv.type = :type)
        AND (ff.facilityId IN :facilityIds OR tf.facilityId IN :facilityIds)
        ORDER BY inv.createDate DESC
        """)

    Page<InvSlipDTO> getInventorySlipDto(Pageable pageable
                                       , @Param("facilityIds") List<String> facilityIds
                                        ,@Param("startOfDay") LocalDateTime startOfDay
                                         , @Param("endOfDay") LocalDateTime endOfDay
                                        , @Param("type") Boolean type //true nhập, false xuất
    );

    @Query("""
    SELECT inv.isid
         , inv.type
         , ff.facilityId
         , ff.facilityName
         , tf.facilityId
         , tf.facilityName
         , inv.status
         , s.staffID
         , s.fullname
         , apv.staffID
         , apv.fullname
         , inv.createDate
         , inv.note
    FROM InventorySlip inv
    JOIN inv.staff s
    LEFT JOIN inv.approver apv
    LEFT JOIN inv.toFacility tf
    LEFT JOIN inv.fromFacility ff
    LEFT JOIN ff.parent pf
    LEFT JOIN pf.parent pz
    LEFT JOIN pf.manager pfm
    LEFT JOIN pz.manager pzm
    WHERE (pfm.staffID = :managerId OR pzm.staffID = :managerId)
      AND (
            tf.type = 'K'
         OR (tf IS NULL AND ff.type = 'K')
         OR (tf IS NULL AND ff.type = 'S')
      )
      AND inv.status = 'PENDING'
    ORDER BY inv.createDate DESC
""")
    List<InvSlipDTO> getPendingSlipOfArea(
            @Param("managerId") String managerId
    );


    @Query("""
        SELECT inv.isid
            ,  inv.type
            , ff.facilityId
            , ff.facilityName
            , tf.facilityId
            , tf.facilityName
            , inv.status
            , s.staffID
            , s.fullname
            , apv.staffID
            , apv.fullname
            , inv.createDate
            , inv.note
        FROM InventorySlip inv
        JOIN inv.staff s
        LEFT JOIN inv.approver apv
        LEFT JOIN inv.toFacility tf
        LEFT JOIN inv.fromFacility ff
        WHERE (
            (tf.type = 'W' AND ff.type = 'W')
            OR (ff.type = 'W' AND tf is null)
        )
      AND inv.status = 'PENDING'
        ORDER BY inv.createDate DESC
        """)
    List<InvSlipDTO> getPendingSlipOfAdmin();

    @Query("""
    SELECT COUNT(inv)
    FROM InventorySlip inv
    JOIN inv.staff s
    LEFT JOIN inv.approver apv
    LEFT JOIN inv.toFacility tf
    LEFT JOIN inv.fromFacility ff
    LEFT JOIN ff.parent pf
    LEFT JOIN pf.parent pz
    LEFT JOIN pf.manager pfm
    LEFT JOIN pz.manager pzm
    WHERE (pfm.staffID = :managerId OR pzm.staffID = :managerId)
      AND (
            tf.type = 'K'
         OR (tf IS NULL AND ff.type = 'K')
         OR (tf IS NULL AND ff.type = 'S')
      )
      AND inv.status = 'PENDING'
    """)
    long countPendingSlipOfArea(@Param("managerId") String managerId);

    @Query("""
    SELECT COUNT(inv)
    FROM InventorySlip inv
    JOIN inv.staff s
    LEFT JOIN inv.approver apv
    LEFT JOIN inv.toFacility tf
    LEFT JOIN inv.fromFacility ff
    WHERE (
            (tf.type = 'W' AND ff.type = 'W')
            OR (ff.type = 'W' AND tf is null)
        )
      AND inv.status = 'PENDING'
    """)
    long countPendingSlipOfAdmin();

    @Query("""
        SELECT inv.isid
            ,  inv.type
            , ff.facilityId
            , ff.facilityName
            , tf.facilityId
            , tf.facilityName
            , inv.status
            , s.staffID
            , s.fullname
            , apv.staffID
            , apv.fullname
            , inv.createDate
            , inv.note
        FROM InventorySlip inv
        JOIN inv.staff s
        LEFT JOIN inv.approver apv
        LEFT JOIN inv.toFacility tf
        LEFT JOIN inv.fromFacility ff
        WHERE inv.status = 'APPROVED'
            AND tf.facilityId = :facilityId
    """)
    List<InvSlipDTO> getApprovedSlips(@Param("facilityId") String facilityId);

    @Query("""
        SELECT COUNT(inv)
        FROM InventorySlip inv
        LEFT JOIN inv.toFacility f
        WHERE f.facilityId = :facilityId
                AND inv.status = 'APPROVED'
        """)
    long countApprovedSlips(@Param("facilityId") String facilityId);




    @Query("""
    SELECT inv.isid
        , i.itemId
        , CONCAT(p.productName, ' - ', v.color, ' - ', i.size.code)
        , invd.quantity
    FROM InventorySlipDetail invd
    JOIN invd.inventorySlip inv
    JOIN invd.item i
    JOIN i.variant v
    JOIN v.product p
    WHERE inv.isid IN :invSlipIds
    """)
    List<InvSlipDetailDTO> getInventorySlipDetailDto(@Param("invSlipIds") List<String> invSlipIds);

    @Query("""
        SELECT i.itemId
                , CONCAT(p.productName, ' - ', v.color, ' - ', i.size.code)
                , inv.quantity
        FROM Inventory inv
        JOIN inv.item i
        JOIN i.variant v
        JOIN v.product p
        JOIN inv.facility f
        WHERE f.facilityId =:facilityId
            AND inv.quantity >1
            AND (
            :keyword IS NULL
            OR :keyword = ''
            OR p.productName LIKE CONCAT('%', :keyword, '%')
            )
        """)
    Page<ItemInvSlipDTO> getItemInvSlipDTO(Pageable pageable
    , @Param("facilityId") String facilityId
    , @Param("keyword") String keyword);

    @Query("""
        SELECT i.itemId
                , CONCAT(p.productName, ' - ', v.color, ' - ', i.size.code)
        FROM Item i
        JOIN i.variant v
        JOIN v.product p
            WHERE (
            :keyword IS NULL
            OR :keyword = ''
            OR p.productName LIKE CONCAT('%', :keyword, '%')
            )
        """)
    Page<ItemInvSlipDTO> getAllItemSlipDTO(Pageable pageable , @Param("keyword") String keyword);

    @Query("""
    SELECT MAX(inv.isid)
    FROM InventorySlip inv 
    WHERE inv.isid LIKE :prefix%
    """)
    String findMaxInvSlipIdByPrefix(@Param("prefix") String prefix);


    @Modifying
    @Transactional
    @Query("UPDATE InventorySlip s SET s.status = 'REJECTED', s.note=concat(s.note,'[Quá giờ 24 giờ chưa được duyệt]') " +
            "WHERE s.status = 'PENDING' AND s.createDate <= :limit")
    void rejectOldSlips(@Param("limit") LocalDateTime limit);


    @Modifying
    @Query("""
    UPDATE InventorySlip inv
    SET inv.status = 'DONE'
    WHERE inv.status = 'APPROVED' AND inv.isid= :id
    """)
    void updateDoneInvSlip(@Param("id") String id);
}
