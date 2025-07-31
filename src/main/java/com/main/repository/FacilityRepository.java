package com.main.repository;

import com.main.dto.FacilityOrdManagerDTO;
import com.main.entity.Facility;
import com.main.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FacilityRepository  extends JpaRepository<Facility, String>  {
    List<Facility> getFacilityByIsUseTrue();

    Collection<? extends Facility> findByParent(Facility parent);

    List<Facility> findByManager_StaffID(String managerStaffID);

    @Query("""
        Select f.facilityId, f.facilityName
        , p.facilityId, p.facilityName
        from Facility f
        JOIN f.parent p
          where f.type = 's'
        """)
    List<FacilityOrdManagerDTO> getShop();


}
