package com.main.repository;

import com.main.entity.Membership;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MembershipRepository  extends JpaRepository<Membership, String> {

    @Query("SELECT m FROM Membership m " +
            "WHERE m.minPoint <= :totalSpent " +
            "ORDER BY m.minPoint DESC")
    List<Membership> findMembershipBySpent(@Param("totalSpent") BigDecimal totalSpent);

}
