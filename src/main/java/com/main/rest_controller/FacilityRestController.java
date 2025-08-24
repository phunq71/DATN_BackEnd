package com.main.rest_controller;

import com.main.dto.FacilityDTO;
import com.main.entity.Facility;
import com.main.service.FacilityService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/facility")
@RequiredArgsConstructor
public class FacilityRestController {
    private final FacilityService facilityService;

    private void checkAdmin() {
        if (!AuthUtil.isLogin() || !"ROLE_ADMIN".equals(AuthUtil.getRole())) {
            throw new SecurityException("Bạn không có quyền thực hiện chức năng này.");
        }
        System.out.println("Login: " + AuthUtil.isLogin() + ", role=" + AuthUtil.getRole());

    }

    // Lấy tất cả cơ sở
    @GetMapping
    public ResponseEntity<List<FacilityDTO>> getAllFacilities() {
        checkAdmin();
        return ResponseEntity.ok(facilityService.getAllFacilities());
    }

    // Lấy thông tin cơ sở theo id
    @GetMapping("/{id}")
    public ResponseEntity<FacilityDTO> getFacility(@PathVariable String id) {
        checkAdmin();
        try {
            return ResponseEntity.ok(facilityService.getFacilityById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Tạo mới cơ sở
    @PostMapping
    public ResponseEntity<FacilityDTO> createFacility(@RequestBody FacilityDTO dto) {
        checkAdmin();
        FacilityDTO created = facilityService.createFacility(dto);
        return ResponseEntity.ok(created);
    }

    // Cập nhật cơ sở
    @PutMapping("/{id}")
    public ResponseEntity<FacilityDTO> updateFacility(@PathVariable String id, @RequestBody FacilityDTO dto) {
        checkAdmin();
        FacilityDTO updated = facilityService.updateFacility(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa cơ sở
    @DeleteMapping("/{id}")
    public ResponseEntity<FacilityDTO> deleteFacility(@PathVariable String id) {
        checkAdmin();
        FacilityDTO result = facilityService.deleteOrDeactivateFacility(id);
        return ResponseEntity.ok(result);
    }


    // API di chuyển cơ sở sang cơ sở/khu vực cha khác
    @PutMapping("/{id}/move")
    public ResponseEntity<String> moveFacility(@PathVariable String id, @RequestParam String newParentId) {
        checkAdmin();
        facilityService.moveFacility(id, newParentId);
        return ResponseEntity.ok("Di chuyển cơ sở thành công");
    }
}
