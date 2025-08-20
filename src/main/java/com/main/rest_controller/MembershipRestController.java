package com.main.rest_controller;

import com.main.dto.MembershipDTO;
import com.main.entity.Membership;
import com.main.service.MembershipService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
public class MembershipRestController {
    private final MembershipService membershipService;

    @GetMapping("/admin/membership/getAll")
    public ResponseEntity<?> getAll() {
        String role = AuthUtil.getRole();
        if(!Objects.equals(role, "ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<MembershipDTO> membershipDTOs = membershipService.findAllMembership();

        return ResponseEntity.ok(membershipDTOs);
    }

    @PostMapping("/admin/membership/create")
    public ResponseEntity<?> create(@RequestBody MembershipDTO membershipDTO) {
        String role = AuthUtil.getRole();

        if(!Objects.equals(role, "ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Membership membership = membershipService.create(membershipDTO);

        return ResponseEntity.ok(membership.getMembershipId());
    }

    @PutMapping("/admin/membership/update")
    public ResponseEntity<?> update(@RequestBody MembershipDTO membershipDTO) {
        String role = AuthUtil.getRole();
        if(!Objects.equals(role, "ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Membership membership = membershipService.update(membershipDTO);
        if(membership == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(membership.getMembershipId());
    }

    @DeleteMapping("/admin/membership/delete")
    public ResponseEntity<?> delete(@RequestParam String membershipId) {
        String role = AuthUtil.getRole();
        if(!Objects.equals(role, "ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if(membershipService.delete(membershipId)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/admin/membership/pie")
    public ResponseEntity<?> getMembershipPie() {
        String role = AuthUtil.getRole();
        if(!Objects.equals(role, "ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(membershipService.getMembershipPie());
    }
}
