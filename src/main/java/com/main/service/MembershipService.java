package com.main.service;

import com.main.dto.MembershipDTO;
import com.main.entity.Membership;

import java.util.List;
import java.util.Optional;

public interface MembershipService {
    public List<MembershipDTO> getAllMemberships();
    public Optional<Membership> getMembershipById(String id);

}
