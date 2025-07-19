package com.main.repository;

import com.main.entity.Membership;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository  extends JpaRepository<Membership, String> {
}
