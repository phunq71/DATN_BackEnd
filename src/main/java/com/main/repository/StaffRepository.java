package com.main.repository;

import com.main.dto.StaffDTO;
import com.main.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {
    Staff getByStaffID(String staffID);

    @Query("""
        SELECT  s.staffID
                , s.fullname
                , a.email
                , a.password
                , s.phone
                , s.dob
                , s.address
                , a.role
                , a.status
                , a.createAt
                , a.updateAt
                , f.facilityId
        FROM Staff s
        JOIN s.account a
        JOIN s.facility f
         WHERE (
                (:shopId IS NULL AND (f.parent.facilityId = :areaId OR f.facilityId = :areaId))
                OR (:shopId IS NOT NULL AND f.facilityId = :shopId)
            )
            AND (:keyWord IS NULL OR LOWER(s.fullname) LIKE LOWER(CONCAT('%', :keyWord, '%')))
        ORDER BY a.role
        
        """)
    public Page<StaffDTO> getAllStaffs(Pageable pageable
                                    , @Param("areaId") String areaId
                                    , @Param("shopId") String shopId
                                    , @Param("keyWord") String keyWord);

    @Query("""
        SELECT  s.staffID
                , s.fullname
                , a.email
                , a.password
                , s.phone
                , s.dob
                , s.address
                , a.role
                , a.status
                , a.createAt
                , a.updateAt
                , f.facilityId
        FROM Staff s
        JOIN s.account a
        LEFT JOIN s.facility f
        WHERE a.role ='ADMIN'
            AND (:keyWord IS NULL OR LOWER(s.fullname) LIKE LOWER(CONCAT('%', :keyWord, '%')))
        """)
    public Page<StaffDTO> getAdmin(Pageable pageable, @Param("keyWord") String keyWord);

    @Query("SELECT s.staffID FROM Staff s ORDER BY s.staffID DESC LIMIT 1")
    String findTopStaffID(); // Nếu đã đảm bảo staffID luôn là STAF + số

    boolean existsByAccount_Email(String email);

    boolean existsByPhone(String phone);

    boolean existsByAccount_EmailAndStaffIDNot(String email, String staffId);
    boolean existsByPhoneAndStaffIDNot(String phone, String staffId);


}
