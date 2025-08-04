package com.main.serviceImpl;


import com.main.entity.Staff;
import com.main.repository.StaffRepository;
import com.main.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import com.main.dto.DemoteStaffDTO;
import com.main.dto.StaffDTO;
import com.main.entity.Account;
import com.main.entity.Facility;
import com.main.entity.Staff;
import com.main.repository.AccountRepository;
import com.main.repository.FacilityRepository;
import com.main.repository.StaffRepository;
import com.main.service.StaffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;
    private final AccountRepository accountRepository;
    private final FacilityRepository facilityRepository;
    private final PasswordEncoder passwordEncoder;
    private final int pageSize = 5;
    @Override
    public Staff getStaffByID(String id) {
        return staffRepository.getByStaffID(id);
    }

    @Override
    public Staff getStaffByAccountID(String id) {
        return staffRepository.getByStaffID(id);
    }


        @Override
        public Page<StaffDTO> getAllStaffs ( int pageNumber, String areaId, String shopId, String keyWord){
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            return staffRepository.getAllStaffs(pageable, areaId, shopId, keyWord);
        }

        @Override
        public Page<StaffDTO> getAdmin ( int pageNumber, String keyWord){
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            return staffRepository.getAdmin(pageable, keyWord);
        }

        @Override
        public boolean checkAreaOfManager (String areaId, String managerId){
            return facilityRepository.existsByFacilityIdAndManager_StaffID(areaId, managerId);
        }

        @Override
        public boolean checkFacilityOfManager (String facilityId, String managerId){
            Facility facility = facilityRepository.findById(facilityId).orElse(null);
            if (facility == null) return false;

            if (facility.getType().equals("Z")
                    && !facility.getManager().getStaffID().equals(managerId)) return false;


            if (facility.getType().equals("S")
                    && !facility.getParent().getManager().getStaffID().equals(managerId)) return false;

            return true;
        }

//    @Override
        public StaffDTO createStaff2 (StaffDTO staffDTO){
            try {


                String id = generateStaffID();
                System.err.println("Chuẩn bị tạo staff id: " + id);
                Account account = new Account();

                account.setAccountId(id);
                account.setEmail(staffDTO.getEmail());
                account.setPassword(staffDTO.getPassword());
                account.setRole(staffDTO.getRole());
                account.setStatus(staffDTO.getStatus());
                account.setCreateAt(LocalDate.now());


                Staff staff = new Staff();
                staff.setStaffID(id);
                staff.setFullname(staffDTO.getFullName());
                staff.setPhone(staffDTO.getPhone());
                staff.setAddress(staffDTO.getFullAddress());
                staff.setDob(staffDTO.getDob());
                if (account.getRole().equals("STOCK")) {
                    staff.setFacility(new Facility(facilityRepository.getWarehouseByAreaId(staffDTO.getFacilityID())));
                } else staff.setFacility(new Facility(staffDTO.getFacilityID()));

                accountRepository.save(account);
                staffRepository.save(staff);


                return staffDTO;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return null;
            }
        }

        public String generateStaffID () {
            String prefix = "STAF";
            int totalLength = 12;
            int numberLength = totalLength - prefix.length();

            String maxStaffId = staffRepository.findTopStaffID();
            int nextNumber = 1;

            if (maxStaffId != null && maxStaffId.startsWith(prefix)) {
                String numberPart = maxStaffId.substring(prefix.length());
                nextNumber = parseSafe(numberPart) + 1;
            }

            String formattedNumber = String.format("%0" + numberLength + "d", nextNumber);
            return prefix + formattedNumber;
        }

        // Tách hàm xử lý an toàn cho dễ test
        private int parseSafe (String numberPart){
            try {
                return Integer.parseInt(numberPart);
            } catch (NumberFormatException e) {
                return 0;
            }
        }


        @Override
        @Transactional
        public StaffDTO createStaff (StaffDTO staffDTO){
            System.err.println("=== BẮT ĐẦU TẠO STAFF ===");

            // Log thông tin cơ bản (không log mật khẩu)
            System.err.println("📥 Dữ liệu StaffDTO nhận được:");
            System.err.println(" - Email: " + staffDTO.getEmail());
            System.err.println(" - Role: " + staffDTO.getRole());
            System.err.println(" - FacilityID: " + staffDTO.getFacilityID());

            // Tạo ID và Account
            String id = generateStaffID();
            System.err.println("✔️ Staff ID được tạo: " + id);

            Account account = new Account();
            account.setAccountId(id);
            account.setEmail(staffDTO.getEmail());
            account.setPassword(passwordEncoder.encode(staffDTO.getPassword())); // Mã hóa mật khẩu
            account.setRole(staffDTO.getRole());
            account.setStatus(staffDTO.getStatus());
            account.setCreateAt(LocalDate.now());

            // Tạo Staff
            Staff staff = new Staff();
            staff.setAccount(account);
            staff.setFullname(staffDTO.getFullName());
            staff.setPhone(staffDTO.getPhone());
            staff.setAddress(staffDTO.getFullAddress());
            staff.setDob(staffDTO.getDob());

            // Xử lý Facility theo role
            if ("STOCK".equals(account.getRole())) {
                String warehouseId = facilityRepository.getWarehouseByAreaId(staffDTO.getFacilityID());
                System.err.println("📦 Role STOCK - WarehouseID: " + warehouseId);
                staff.setFacility(new Facility(warehouseId));
            } else if ("ADMIN".equals(account.getRole())) {
                staff.setFacility(null);
            } else {
                System.err.println("🏢 Role khác - FacilityID dùng: " + staffDTO.getFacilityID());
                staff.setFacility(new Facility(staffDTO.getFacilityID()));
            }

            // Lưu vào database
            staffRepository.save(staff);
            System.err.println("✅ Đã lưu staff thành công - ID: " + id);
            Facility facility = facilityRepository.findById(staff.getFacility().getFacilityId()).get();
            if ("AREA".equals(account.getRole()) && facility.getType().equals("Z")) {
                facility.setManager(staff);
                facilityRepository.save(facility);
                System.err.println("👉Đã cập nhật lại manager: " + staff.getStaffID() + "👉👉" + facility.getFacilityId());
            }


            return staffDTO;
        }


        @Override
        @Transactional
        public StaffDTO updateStaff (StaffDTO staffDTO){
            System.err.println("=== BẮT ĐẦU CẬP NHẬT STAFF ===");

            // 1. Tìm staff và account
            Staff staff = staffRepository.findById(staffDTO.getStaffID())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + staffDTO.getStaffID()));

            Account account = staff.getAccount();
            String oldRole = account.getRole();

            // 2. Cập nhật account
            account.setEmail(staffDTO.getEmail());
            if (staffDTO.getPassword() != null && !staffDTO.getPassword().isBlank()) {
                account.setPassword(passwordEncoder.encode(staffDTO.getPassword()));
            }
            account.setRole(staffDTO.getRole());
            account.setStatus(staffDTO.getStatus());
            account.setUpdateAt(LocalDate.now());

            // Nếu chuyển từ AREA sang role khác và nhân viên này đang là manager thì set lại
            if ("AREA".equals(oldRole) && !"AREA".equals(account.getRole())) {
                Facility managedFacility = facilityRepository.findByManager_StaffID(staff.getStaffID());
                if (managedFacility != null) {
                    managedFacility.setManager(null);
                    facilityRepository.save(managedFacility);
                    System.err.println("❌ Đã gỡ vai trò manager khỏi facility: " + managedFacility.getFacilityId());
                }
            }

            // 3. Cập nhật staff
            staff.setFullname(staffDTO.getFullName());
            staff.setPhone(staffDTO.getPhone());
            staff.setAddress(staffDTO.getFullAddress());
            staff.setDob(staffDTO.getDob());

            // 4. Cập nhật facility theo role
            if ("STOCK".equals(account.getRole())) {
                System.err.println("role stock");
                System.err.println("facility id: " + staff.getFacility().getFacilityId());
                String warehouseId = facilityRepository.getWarehouseByAreaId(staffDTO.getFacilityID());
                System.err.println("📦 Role STOCK - WarehouseID: " + warehouseId);
                staff.setFacility(new Facility(warehouseId));
            } else if ("ADMIN".equals(account.getRole())) {
                staff.setFacility(null);
            } else {
                System.err.println("🏢 Role khác - FacilityID dùng: " + staffDTO.getFacilityID());
                staff.setFacility(new Facility(staffDTO.getFacilityID()));
            }

            // 5. Cập nhật manager nếu là AREA
            Facility facility = facilityRepository.findById(staffDTO.getFacilityID()).get();
            if ("AREA".equals(account.getRole()) && facility.getType().equals("Z")) {
                Staff currentManager = facility.getManager();
                if (currentManager == null || !currentManager.getStaffID().equals(staff.getStaffID())) {
                    facility.setManager(staff);
                    facilityRepository.save(facility);
                    System.err.println("👉Đã cập nhật lại manager: " + staff.getStaffID() + " 👉👉 " + facility.getFacilityId());
                }

            }

            // 6. Lưu lại staff và account
            staffRepository.save(staff);

            System.err.println("✅ Đã cập nhật staff thành công - ID: " + staff.getStaffID());
            return staffDTO;
        }


        @Override
        public boolean demoteStaff (DemoteStaffDTO demoteStaffDTO){
            System.err.println("⭐⭐Tiến hành giáng chức nhân viên: " + demoteStaffDTO);
            Facility facility = facilityRepository.findById(demoteStaffDTO.getAreaId()).orElse(null);
            if (facility != null) {
                Staff staff = facility.getManager();

                Account account = staff.getAccount();
                account.setRole(demoteStaffDTO.getNewRole());
                accountRepository.save(account);

                if (demoteStaffDTO.getShopId() != null && !demoteStaffDTO.getShopId().isEmpty()) {
                    staff.setFacility(new Facility(demoteStaffDTO.getShopId()));

                } else {
                    staff.setFacility(new Facility(demoteStaffDTO.getAreaId()));
                }
                staffRepository.save(staff);
                facility.setManager(null);
                facilityRepository.save(facility);
                return true;
            } else return false;
        }

        @Override
        @Transactional
        public boolean deleteStaff (String staffID,boolean isManager){
            if (canDeleteStaff(staffID)) {
                System.err.println("được xóa nhân viên: " + staffID);
                if (isManager) {
                    System.err.println("Đây là quản lý");
                    Facility facility = facilityRepository.findByManager_StaffID(staffID);
                    facility.setManager(null);
                    facilityRepository.save(facility);
                    System.err.println("Đã update lại manager null");
                }

                System.err.println("tiền hành xóa staff");
                staffRepository.deleteById(staffID);
                System.err.println("đã xóa");
                return true;
            }
            return false;
        }

        private boolean canDeleteStaff (String staffID){
            Staff staff = staffRepository.findById(staffID)
                    .orElseThrow(() -> new RuntimeException("Staff not found"));

            return
                    staff.getInventorySlips().isEmpty()
                            && staff.getApprover_inventorSlips().isEmpty()
                            && staff.getTransactions().isEmpty()
                            && staff.getApprover_transactions().isEmpty()
                            && staff.getReturRequests().isEmpty()
                            && staff.getOrders().isEmpty();

        }
    }

