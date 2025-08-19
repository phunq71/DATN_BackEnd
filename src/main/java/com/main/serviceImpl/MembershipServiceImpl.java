package com.main.serviceImpl;

import com.main.dto.MembershipDTO;
import com.main.repository.MembershipRepository;
import com.main.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    private final MembershipRepository membershipRepository;
    @Override
    public List<MembershipDTO> getAllMemberships() {
        return membershipRepository.findAllMemberships();
    }
}
