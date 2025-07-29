package com.main.rest_controller;

import com.main.dto.RevenueByAreaDTO;
import com.main.dto.RevenueByCategoryDTO;
import com.main.dto.RevenueByTimeDTO;
import com.main.entity.Transaction;
import com.main.repository.FacilityRepository;
import com.main.repository.TransactionRepository;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class Admin_Dashboard {

    private final TransactionRepository transactionRepository;
    private final FacilityRepository facilityRepository;

    @GetMapping("/opulentia_admin/dashboard/revenueByYear")
    public ResponseEntity<?> revenueByYear() {
        if(AuthUtil.getRole().equals("ROLE_ADMIN")) {
            List<RevenueByTimeDTO> revenueByYear = transactionRepository.getRevenueByYear();
            return ResponseEntity.ok(revenueByYear);
        }
        else if(AuthUtil.getRole().equals("ROLE_AREA")) {
            System.err.println(AuthUtil.getAccountID());
            System.err.println(AuthUtil.getFullName());

            List<RevenueByTimeDTO> revenueByYear = transactionRepository.getRevenueByYear(AuthUtil.getAccountID());
            String facilityName = facilityRepository.getFacilityNameByManagerId(AuthUtil.getAccountID());

            return ResponseEntity.ok(Map.of(
                    "facilityName", facilityName,
                    "revenueByYear", revenueByYear
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/admin/dashboard/revenueByMonth/{year}")

    public ResponseEntity<?> revenueByMonth(@PathVariable int year) {
        if(AuthUtil.getRole().equals("ROLE_ADMIN")) {
            List<RevenueByTimeDTO> revenueByMonth = transactionRepository.getRevenueByMonth(year);
            return ResponseEntity.ok(revenueByMonth);
        }
        else if(AuthUtil.getRole().equals("ROLE_AREA")) {
            List<RevenueByTimeDTO> revenueByMonth = transactionRepository.getRevenueByMonth(year, AuthUtil.getAccountID());
            return ResponseEntity.ok(revenueByMonth);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @GetMapping("/admin/dashboard/revenue/availableYear")
    public ResponseEntity<?> availableYearMonth() {
        if(AuthUtil.getRole().equals("ROLE_ADMIN")) {
            List<Integer> availableYear = transactionRepository.getAvailableYear();
            return ResponseEntity.ok(availableYear);
        }
        else if(AuthUtil.getRole().equals("ROLE_AREA")) {
            List<Integer> availableYear = transactionRepository.getAvailableYear(AuthUtil.getAccountID());
            return ResponseEntity.ok(availableYear);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/admin/dashboard/revenueByArea")
    public ResponseEntity<?> revenueByArea(
            @RequestParam(value = "year", required = false) Integer year
    ) {
        if(AuthUtil.getRole().equals("ROLE_ADMIN")) {

            if (year == null) {
                List<RevenueByAreaDTO> revenueByArea = transactionRepository.getRevenueByArea();
                return ResponseEntity.ok(revenueByArea);
            }

            List<RevenueByAreaDTO> revenueByArea = transactionRepository.getRevenueByArea(year);
            return ResponseEntity.ok(revenueByArea);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/admin/dashboard/revenueByCategory")
    public ResponseEntity<?> revenueByYear(@RequestParam(value = "year", required = true) Integer year) {
        if(!Objects.equals(AuthUtil.getRole(), "ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<RevenueByCategoryDTO> revenues = transactionRepository.getRevenueBySubCategoryAndYear(year);
        return ResponseEntity.ok(revenues);
    }

    @GetMapping("/admin/dashboard/revenueByCategory/availableYears")
    public ResponseEntity<?> availableYearCategory() {
        if(!Objects.equals(AuthUtil.getRole(), "ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(transactionRepository.getAvailableYearCategory());
    }

}
