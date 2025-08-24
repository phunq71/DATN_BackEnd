package com.main.serviceImpl;

import com.main.dto.MembershipDTO;



import com.main.dto.MembershipDTO_Pie;

import com.main.entity.Membership;
import com.main.repository.MembershipRepository;
import com.main.service.MembershipService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    private final MembershipRepository membershipRepository;

    @Override
    public List<MembershipDTO> findAllMembership() {
        return membershipRepository.findAllMembership();
    }

    @Override
    @Transactional
    public Membership create(MembershipDTO membershipDTO) {
        Membership membership = new Membership(generateId(), membershipDTO.getRank(), membershipDTO.getDescription(), membershipDTO.getMinPoint());
        return membershipRepository.save(membership);
    }

    @Override
    @Transactional
    public Membership update(MembershipDTO membershipDTO) {
        Membership membership = membershipRepository.findById(membershipDTO.getId()).orElse(null);

        if (membership == null) {
            return null;
        }
        membership.setDescription(membershipDTO.getDescription());
        membership.setRank(membershipDTO.getRank());
        membership.setMinPoint(membershipDTO.getMinPoint());
        return membershipRepository.save(membership);
    }

    @Override
    public boolean delete(String id) {
        if(!membershipRepository.existsById(id)) {
            return false;
        }

        membershipRepository.deleteById(id);
        return true;
    }

    @Override
    public List<MembershipDTO_Pie> getMembershipPie() {
        return membershipRepository.getMembershipPie();
    }

    private String generateId() {
        String maxId = membershipRepository.getMaxId();

        if (maxId == null) {
            return "MB01";
        }

        int idNumber = Integer.parseInt(maxId.substring(2));
        idNumber++;

        return String.format("MB%02d", idNumber);
    }

    @Override
    public List<MembershipDTO> getAllMemberships() {
        return membershipRepository.findAllMemberships();
    }

    @Override
    public Optional<Membership> getMembershipById(String id) {
        return membershipRepository.findById(id);
    }

}
