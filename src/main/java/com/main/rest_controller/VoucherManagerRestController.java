package com.main.rest_controller;

import com.main.dto.MembershipDTO;
import com.main.dto.PromotionProductManagerDTO;
import com.main.dto.PromotionVoucherManagerDTO;
import com.main.dto.VoucherManagermetDTO;
import com.main.entity.*;
import com.main.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class VoucherManagerRestController {
    private final PromotionService promotionService;
    private final MembershipService membershipService;
    private final PromotionProductService promotionProductService;
    private final VoucherService voucherService;
    private final ProductService productService;

    @GetMapping("/admin/Promotion/loadData")
    public ResponseEntity<?> loadPromotion(){
        List<PromotionVoucherManagerDTO> promotions = promotionService.getPromotions();
        return ResponseEntity.ok(promotions);
    }
    @GetMapping("/admin/Promotion/memberShip")
    public List<MembershipDTO> getMemberships() {
        return membershipService.getAllMemberships();
    }

    @PostMapping("/admin/Promotion/createPromotion")
    public ResponseEntity<?> createPromotion(
            @RequestParam(required = false) String promotionID,
            @RequestParam(required = false) String promotionName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String membershipId) {

        System.err.println(
                promotionID + " " + promotionName + " " + description + " " +
                        startDate + " " + endDate + " " + " " + type + " " + membershipId
        );
        LocalDate start = LocalDate.parse(startDate); // parse yyyy-MM-dd
        LocalDate end = LocalDate.parse(endDate);

        LocalDateTime startDatee = start.atStartOfDay();           // 2025-08-17T00:00:00
        LocalDateTime endDatee = end.atTime(23, 59, 59);           // 2025-08-17T23:59:59

        Promotion promotion = new Promotion();
        promotion.setPromotionID(promotionID);
        promotion.setPromotionName(promotionName);
        promotion.setDescription(description);
        promotion.setCreateAt(LocalDateTime.now());
        promotion.setStartDate(startDatee);
        promotion.setEndDate(endDatee);
        promotion.setType(type);

        if (membershipId != null && !membershipId.isEmpty()) {
            Optional<Membership> membershipOpt = membershipService.getMembershipById(membershipId);
            if (membershipOpt.isPresent()) {
                promotion.setMembership(membershipOpt.get());
            } else {
                return ResponseEntity
                        .badRequest()
                        .body("Membership ID không tồn tại: " + membershipId);
            }
        }
         promotionService.save(promotion);
        return ResponseEntity.ok("ok");
    }
    @PostMapping("/admin/Promotion/updatePromotion")
    public ResponseEntity<?> updatePromotion(
            @RequestParam(required = false) String promotionID,
            @RequestParam(required = false) String promotionName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String membershipId) {

        System.err.println(
                promotionID + " " + promotionName + " " + description + " " +
                        startDate + " " + endDate + " " + " " + type + " " + membershipId
        );
        LocalDate start = LocalDate.parse(startDate); // parse yyyy-MM-dd
        LocalDate end = LocalDate.parse(endDate);

        LocalDateTime startDatee = start.atStartOfDay();           // 2025-08-17T00:00:00
        LocalDateTime endDatee = end.atTime(23, 59, 59);           // 2025-08-17T23:59:59

        Promotion promotion = promotionService.getByPromotionId(promotionID);
        promotion.setPromotionName(promotionName);
        promotion.setDescription(description);
        promotion.setStartDate(startDatee);
        promotion.setEndDate(endDatee);
        promotion.setType(type);

        if (membershipId != null && !membershipId.isEmpty()) {
            Optional<Membership> membershipOpt = membershipService.getMembershipById(membershipId);
            if (membershipOpt.isPresent()) {
                promotion.setMembership(membershipOpt.get());
            } else {
                return ResponseEntity
                        .badRequest()
                        .body("Membership ID không tồn tại: " + membershipId);
            }
        }
        promotionService.save(promotion);
        return ResponseEntity.ok("ok");
    }
    @GetMapping("/admin/Promotion/PromotionProduct/{promotionId}")
    public ResponseEntity<?> PromotionProduct(
                            @PathVariable String promotionId
                            ) {
        if(promotionId != null) {
            List<PromotionProductManagerDTO> promotionProductManagerDTOS = promotionProductService.findPromotionByPromotionId(promotionId);
            return ResponseEntity.ok(promotionProductManagerDTOS);
        }
        return ResponseEntity.status(404).build();
    }
    @GetMapping("/admin/Promotion/Voucher/{promotionId}")
    public ResponseEntity<?> VoucherProduct(@PathVariable String promotionId) {
        List<VoucherManagermetDTO> vouchers= voucherService.findVoucherByPromotionId(promotionId);
        return ResponseEntity.ok(vouchers);
    }
    @GetMapping("/admin/Promotion/promotionIdNew")
    public ResponseEntity<?> PromotionGetID() {
        String idPromotion = promotionService.findTop1ByOrderByPromotionIDDesc();
//            // Cắt phần số sau "KM"
            String numberPart = idPromotion.substring(2);
            int nextId = Integer.parseInt(numberPart) + 1;
//            // Format lại với padding 6 số
            String newVoucherId = "KM" + String.format("%06d", nextId);
            return ResponseEntity.ok(newVoucherId);
    }

//    -------------------- Phần voucher nhe
@PostMapping("/admin/Promotion/createVoucher")
public ResponseEntity<?> createVoucher(
        @RequestParam(required = false) String voucherId,
        @RequestParam(required = false) String discountType,
        @RequestParam(required = false) Integer discountValue,
        @RequestParam(required = false) BigDecimal minOrderValue,
        @RequestParam(required = false) Integer quantityUsed,
        @RequestParam(required = false) Integer quantityRemaining,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) BigDecimal claimConditions,
        @RequestParam(required = false) String promotionId) {
    System.err.println( "VoucherCampaing"+
        " " + voucherId + " " + discountType+ " " +discountValue+ " "
            + minOrderValue+ " " +quantityUsed+ " " +quantityRemaining+ " "
            + endDate+ " " +claimConditions+ " " +promotionId
    );

    Voucher voucher = new Voucher();
    Promotion promotion = promotionService.getByPromotionId(promotionId);
    voucher.setVoucherID(voucherId);
    voucher.setDiscountType(discountType);
    voucher.setDiscountValue(discountValue);
    voucher.setMinOrderValue(minOrderValue);
    voucher.setQuantityUsed(quantityUsed);
    voucher.setQuantityRemaining(quantityRemaining);
    if (endDate != null && !endDate.isEmpty()) {
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime endDateTime = end.atTime(23, 59, 59);
        voucher.setEndDate(endDateTime);
    } else {
        voucher.setEndDate(null);
    }
    voucher.setClaimConditions(claimConditions);
    voucher.setPromotion(promotion);
    if(promotion.getType().equals("VoucherCampaign")) {
        voucher.setType(true);
    }else{
        voucher.setType(false);
    }
    voucherService.save(voucher);
    System.err.println("Thêm voucher than ồng: " + voucher.getVoucherID());
    return ResponseEntity.ok("ok");
}
    @PostMapping("/admin/Promotion/updateVoucherCampaign")
    public ResponseEntity<?> updateVoucherCampaign(
            @RequestParam(required = false) String voucherId,
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false) Integer discountValue,
            @RequestParam(required = false) BigDecimal minOrderValue,
            @RequestParam(required = false) Integer quantityUsed,
            @RequestParam(required = false) Integer quantityRemaining,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) BigDecimal claimConditions,
            @RequestParam(required = false) String promotionId) {
        System.err.println( "VoucherCampaing"+
                " " + voucherId + " " + discountType+ " " +discountValue+ " "
                + minOrderValue+ " " +quantityUsed+ " " +quantityRemaining+ " "
                + endDate+ " " +claimConditions+ " " +promotionId
        );
        Voucher voucher = voucherService.findVoucherById(voucherId);
        Promotion promotion = promotionService.getByPromotionId(promotionId);
        voucher.setDiscountType(discountType);
        voucher.setDiscountValue(discountValue);
        voucher.setMinOrderValue(minOrderValue);
        voucher.setQuantityRemaining(quantityRemaining);
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime endDateTime = end.atTime(23, 59, 59);
            voucher.setEndDate(endDateTime);
        }
        else {
            voucher.setEndDate(null);
        }
        if(promotion.getType().equals("VoucherCampaign")) {
            voucher.setType(true);
        }else{
            voucher.setType(false);
        }
        voucherService.save(voucher);
        System.err.println("Update voucher than cồng: " + voucher.getVoucherID());
        return ResponseEntity.ok("ok");
    }
    @GetMapping("/admin/Promotion/voucherIdNew")
    public ResponseEntity<?> VoucherGetID() {
        String voucherIdNew= voucherService.findMaxVoucherId();
//            // Cắt phần số sau "VC"
        String numberPart = voucherIdNew.substring(2);
        int nextId = Integer.parseInt(numberPart) + 1;
//            // Format lại với padding 6 số
        String newVoucherId = "VC" + String.format("%06d", nextId);
        return ResponseEntity.ok(newVoucherId);
    }
    @GetMapping("/admin/Promotion/DeleteVoucher/{voucherId}")
    public ResponseEntity<?> deleteVoucher(@PathVariable("voucherId") String voucherId) {
        System.err.println("Vou chẻ id nè: "+voucherId);
        try {
            boolean deleted = voucherService.deleteVoucher(voucherId);
            if (deleted) {
                return ResponseEntity.ok("Xoá voucher " + voucherId + " thành công!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Voucher với ID " + voucherId + " không tồn tại!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi xoá voucher: " + e.getMessage());
        }
    }
    @GetMapping("/admin/Promotion/promotionProduct/{startDate}/{endDate}/{promotionId}")
    public ResponseEntity<?> PromotionGetProducts(@PathVariable String startDate,
                                                  @PathVariable String endDate,
                                                  @PathVariable String promotionId) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime startDateTime = start.atTime(00, 00, 00);
        LocalDateTime endDateTime = end.atTime(23, 59, 59);
        List<String> list1 = promotionProductService.findProductOfNewPromotion(startDateTime, endDateTime, promotionId);
        return ResponseEntity.ok(list1);
    }
    @PostMapping("/admin/Promotion/createPromotionProduct")
    public ResponseEntity<?> createPromotionProduct(
            @RequestParam(required = false) String promotionId,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) Integer quantityUsed,
            @RequestParam(required = false) Integer quantityRemaining,
            @RequestParam(required = false) Byte discountPercent,
            @RequestParam(required = false) String note) {
        System.out.println(promotionId + " " + productId + " " + quantityUsed + " " + quantityRemaining + " " + discountPercent+ " " + note);
        PromotionProduct promotionProduct = new PromotionProduct();
        Promotion promotion = promotionService.getByPromotionId(promotionId);
        Product product = productService.findByProductID(productId).get();
        promotionProduct.setPromotion(promotion);
        promotionProduct.setProduct(product);
        promotionProduct.setQuantityUsed(quantityUsed);
        promotionProduct.setQuantityRemaining(quantityRemaining);
        promotionProduct.setDiscountPercent(discountPercent);
        promotionProduct.setNote(note);
        promotionProductService.savePromotionProduct(promotionProduct);
        return ResponseEntity.ok("ok");
    }
    @PostMapping("/admin/Promotion/updatePromotionProduct")
    public ResponseEntity<?> updatePromotionProduct(
            @RequestParam(required = false) Integer promotionProductId,
            @RequestParam(required = false) String promotionId,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) Integer quantityUsed,
            @RequestParam(required = false) Integer quantityRemaining,
            @RequestParam(required = false) Byte discountPercent,
            @RequestParam(required = false) String note) {
        System.out.println(promotionProductId+" "+promotionId + " " + productId + " " + quantityUsed + " " + quantityRemaining + " " + discountPercent+ " " + note);
        PromotionProduct promotionProduct = promotionProductService.findPromotionProductByPromotionId(promotionProductId);
        Product product = productService.findByProductID(productId).get();
        promotionProduct.setProduct(product);
        promotionProduct.setQuantityUsed(quantityUsed);
        promotionProduct.setQuantityRemaining(quantityRemaining);
        promotionProduct.setDiscountPercent(discountPercent);
        promotionProduct.setNote(note);
        promotionProductService.savePromotionProduct(promotionProduct);
        return ResponseEntity.ok("ok");
    }
    @GetMapping("/admin/Promotion/DeletePromotionProduct/{promotionProductID}")
    public ResponseEntity<?> DeletePromotionProduct(@PathVariable("promotionProductID") Integer promotionProductID) {
        System.err.println("Vou chẻ id nè: " + promotionProductID);
        try {

            boolean deleted = promotionProductService.deletePromotionProduct(promotionProductID);
            if (deleted) {
                return ResponseEntity.ok("Xoá voucher " + promotionProductID + " thành công!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Voucher với " + promotionProductID + " đã được dùng, không thể xóa!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi xoá voucher: " + e.getMessage());
        }
    }
    @GetMapping("/admin/Promotion/DeletePromotion/{promotionID}")
    public ResponseEntity<?> DeletePromotion(@PathVariable("promotionID") String promotionID) {
            boolean deleted = promotionService.deletePromotion(promotionID);
            if (deleted) {
                return ResponseEntity.ok("ok");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Voucher với " + promotionID + " đã được dùng, không thể xóa!");
            }
    }
}
