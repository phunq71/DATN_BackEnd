package com.main.rest_controller;

import com.main.dto.AreaDTO;
import com.main.dto.DemoteStaffDTO;
import com.main.dto.StaffDTO;
import com.main.entity.Account;
import com.main.repository.AccountRepository;
import com.main.repository.FacilityRepository;
import com.main.repository.StaffRepository;
import com.main.service.StaffService;
import com.main.utils.AuthUtil;
import com.main.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class Admin_StaffMangement {

    private final StaffService staffService;
    private final StaffRepository staffRepository;
    private final FacilityRepository facilityRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/admin/staffs/getArea")
    public ResponseEntity<?> getArea() {
        String role = AuthUtil.getRole();
        List<AreaDTO> areas = new ArrayList<>();
        if (Objects.equals(role, "ROLE_ADMIN")) {
            areas = facilityRepository.getArea(null);
        } else if (Objects.equals(role, "ROLE_AREA")) {
            areas = facilityRepository.getArea(AuthUtil.getAccountID());
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(Map.of(
                "role", role,
                "areas", areas
        ));
    }

    @GetMapping("/admin/staffs/getStaffs")
    public ResponseEntity<Page<StaffDTO>> getStaff(@RequestParam(value = "areaId", required = true) String areaId
            , @RequestParam(value = "shopId", required = false) String shopId
            , @RequestParam(value = "pageNumber", required = true) Integer pageNumber
            , @RequestParam(value = "keyWord", required = false) String keyWord
    ) {
//        System.err.println("🚩🚩areaId: " + areaId);
//        System.err.println("shopId: " + shopId);
//        System.err.println("pageNumber: " + pageNumber);
//        System.err.println("keyWord: " + keyWord);
        String role = AuthUtil.getRole();

        if (!Objects.equals(role, "ROLE_ADMIN") && !Objects.equals(role, "ROLE_AREA")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (Objects.equals(role, "ROLE_ADMIN")) {
            if (Objects.equals(areaId, "ADMIN")) {
                System.err.println("trả về admin");

                return ResponseEntity.ok(staffService.getAdmin(pageNumber, keyWord));
            }

            return ResponseEntity.ok(staffService.getAllStaffs(pageNumber, areaId, shopId, keyWord));

        }

        if (!staffService.checkAreaOfManager(areaId, AuthUtil.getAccountID())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(staffService.getAllStaffs(pageNumber, areaId, shopId, keyWord));
    }

    @PostMapping("/admin/staffs/createStaff")
    @Transactional
    public ResponseEntity<?> createStaff(@RequestBody StaffDTO staffDTO) {
        String role = AuthUtil.getRole();
        String accountID = AuthUtil.getAccountID();

        if (!Objects.equals(role, "ROLE_ADMIN") && !Objects.equals(role, "ROLE_AREA")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (!ValidationUtils.validateStaffDTO(staffDTO)) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Dữ liệu không hợp lệ!"
            ));
        }

        String staffRole = staffDTO.getRole();
        if (Objects.equals(role, "ROLE_AREA") &&
                (
                    "ADMIN".equals(staffRole)
                    || "AREA".equals(staffRole)
                    || !staffService.checkFacilityOfManager(staffDTO.getFacilityID(), accountID)
                )
            ) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        boolean emailExists = staffRepository.existsByAccount_Email(staffDTO.getEmail());
        boolean phoneExists = staffRepository.existsByPhone(staffDTO.getPhone());

        if (emailExists && phoneExists) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Email và số điện thoại đã tồn tại!"
            ));
        } else if (emailExists) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Email đã tồn tại!"
            ));
        } else if (phoneExists) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Số điện thoại đã tồn tại!"
            ));
        }

        StaffDTO savedStaff= staffService.createStaff(staffDTO);

        if (savedStaff == null) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Lỗi khi lưu, thử lại sau!"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Đã thêm thành công!"
                ,"data", staffDTO
        ));
    }

    @PutMapping("/admin/staffs/demoteStaff")
    @Transactional
    public ResponseEntity<Boolean> demoteStaff(@RequestBody DemoteStaffDTO demoteStaffDTO) {
        String role = AuthUtil.getRole();
        if (!Objects.equals(role, "ROLE_ADMIN")){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        boolean isSaved= staffService.demoteStaff(demoteStaffDTO);

        return ResponseEntity.ok(isSaved);
    }

    @PutMapping("/admin/staffs/updateStaff")
    @Transactional
    public ResponseEntity<?> updateStaff(@RequestBody StaffDTO staffDTO) {
        String role = AuthUtil.getRole();
        String accountID = AuthUtil.getAccountID();

        Account account = accountRepository.findByAccountId(accountID).get();

        if (!Objects.equals(role, "ROLE_ADMIN") && !Objects.equals(role, "ROLE_AREA")) {
            System.err.println("ko có quyền");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(staffDTO.getStaffID().equals(accountID) && !staffDTO.getStatus()) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Bạn không thể tắt trạng thái hoạt động tài khoản của mình!"
            ));
        }

        if (!ValidationUtils.validateStaffDTO(staffDTO)) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Dữ liệu không hợp lệ!"
            ));
        }

        String staffRole = staffDTO.getRole();
        if (Objects.equals(role, "ROLE_AREA") &&
                (
                        "ADMIN".equals(staffRole)
                                || ("AREA".equals(staffRole) && !Objects.equals(accountID, staffDTO.getStaffID()))
                                || !staffService.checkFacilityOfManager(staffDTO.getFacilityID(), accountID)
                )
        ) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        boolean emailExists = staffRepository.existsByAccount_EmailAndStaffIDNot(staffDTO.getEmail(), staffDTO.getStaffID());
        boolean phoneExists = staffRepository.existsByPhoneAndStaffIDNot(staffDTO.getPhone(), staffDTO.getStaffID());

        if (emailExists && phoneExists) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Email và số điện thoại đã tồn tại!"
            ));
        } else if (emailExists) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Email đã tồn tại!"
            ));
        } else if (phoneExists) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Số điện thoại đã tồn tại!"
            ));
        }

        if(Objects.equals(accountID, staffDTO.getStaffID()) && !account.getRole().equals(staffDTO.getRole())){
            staffDTO.setRole(account.getRole());
            StaffDTO savedStaff= staffService.updateStaff(staffDTO);

            if (savedStaff == null) {
                return ResponseEntity.ok(Map.of(
                        "status", "error",
                        "message", "Lỗi khi lưu, thử lại sau!"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "warning",
                    "message", "Bạn không có quyền cập nhật vai trò của mình! Đã cập nhập các thông tin khác"
            ));
        }

        StaffDTO savedStaff= staffService.updateStaff(staffDTO);

        if (savedStaff == null) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Lỗi khi lưu, thử lại sau!"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Cập nhật thành công!"
                ,"data", staffDTO
        ));
    }

    @PostMapping("/admin/staffs/deleteStaff")
    public ResponseEntity<?> deleteStaff(@RequestBody StaffDTO staffDTO) {
        String role = AuthUtil.getRole();
        String accountID = AuthUtil.getAccountID();

        if (!Objects.equals(role, "ROLE_ADMIN") && !Objects.equals(role, "ROLE_AREA")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(Objects.equals(accountID, staffDTO.getStaffID())) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Bạn không thể xóa tài khoản của bạn!"
            ));
        }



        String staffRole = staffDTO.getRole();

        if (Objects.equals(role, "ROLE_AREA") &&
                (
                        "ADMIN".equals(staffRole)
                                || "AREA".equals(staffRole)
                                || !staffService.checkFacilityOfManager(staffDTO.getFacilityID(), accountID)
                )
        ) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(staffService.deleteStaff(staffDTO.getStaffID(), staffDTO.getRole().equals("AREA"))) {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Đã xóa thành công nhân viên: "+staffDTO.getStaffID()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "warning",
                "message", "Vì 1 số ràng buộc bạn không thể xóa nhân viên này!",
                "text", "Bạn có hãy tắt trạng thái hoạt động của tài khoản này nếu không muốn dùng đến!"
        ));
    }
}
