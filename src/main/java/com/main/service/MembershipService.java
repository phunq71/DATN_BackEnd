package com.main.service;


import com.main.dto.MembershipDTO;
import com.main.dto.MembershipDTO_Pie;
import com.main.entity.Membership;

import java.util.List;

public interface MembershipService {

    List<MembershipDTO> findAllMembership();

    Membership create(MembershipDTO membershipDTO);

    Membership update(MembershipDTO membershipDTO);

    boolean delete(String membershipId);

    List<MembershipDTO_Pie> getMembershipPie();

    public List<MembershipDTO> getAllMemberships();

}
