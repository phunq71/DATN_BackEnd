package com.main.repository;

import com.main.dto.MembershipDTO;
import com.main.dto.MembershipDTO_Pie;
import com.main.entity.Membership;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipRepository  extends JpaRepository<Membership, String> {

    @Query("""
        SELECT m.membershipId
            , m.rank
            , m.description
            , m.minPoint
        FROM Membership m
        """)
    List<MembershipDTO> findAllMembership();

    @Query("""
        SELECT MAX(m.membershipId) FROM Membership m
        """)
    String getMaxId();

    @Query("""
        SELECT m.rank,
            COUNT(c.membership)
        FROM Membership m
        JOIN Customer c on c.membership = m
        GROUP BY m.rank
        """)
    List<MembershipDTO_Pie> getMembershipPie();
}
