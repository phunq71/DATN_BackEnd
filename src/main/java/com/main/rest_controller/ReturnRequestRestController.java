package com.main.rest_controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.dto.ReturnItemDTO;
import com.main.dto.ReturnItemFormDTO;
import com.main.dto.ReturnRequestDTO;
import com.main.service.ReturnItemService;
import com.main.service.ReturnRequestService;
import com.main.utils.AuthUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReturnRequestRestController {
    private final ReturnItemService returnItemService;
    private final ReturnRequestService returnRequestService;
    @GetMapping("/opulentia_user/returnItem/get/{orderID}")
    public ResponseEntity<List<ReturnItemDTO>> getReturnItemByOrderID(@PathVariable int orderID) {

        String accountId = AuthUtil.getAccountID();


        if(!returnItemService.checkOrderAndCustomer(orderID, accountId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<ReturnItemDTO> returnItems = returnItemService.getReturnItemByOrderID(orderID);

        if(returnItems.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(returnItems);
    }


    @PostMapping("/opulentia_user/returnItem/save/{orderID}")
    public ResponseEntity<?> saveReturnItem(
            @PathVariable int orderID,
            @RequestParam("returnItems") String returnItems,
            @RequestParam("files") List<MultipartFile> files
    ) {
        String accountId = AuthUtil.getAccountID();
        if(!returnItemService.checkOrderAndCustomer(orderID, accountId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ReturnItemFormDTO> items = mapper.readValue(
                    returnItems, new TypeReference<List<ReturnItemFormDTO>>() {}
            );

        if(returnItemService.saveReturnItem(orderID, items, accountId, files)) {
            return ResponseEntity.ok("OK");
        }
        else throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Có lỗi rồi!");
        }
    }

    @GetMapping("/opulentia_user/returnRequest/get/{year}")
    public ResponseEntity<List<ReturnRequestDTO>> getReturnRequestByOrderID( @PathVariable int year) {
        String accountId = AuthUtil.getAccountID();

        if(accountId == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(returnRequestService.getReturnRequestByCustomerID(accountId, year));
    }

        @GetMapping("/opulentia_user/returnRequestDetail/get/{id}")
    public ResponseEntity<ReturnRequestDTO> getReturnRequestDetail(@PathVariable int id) {
        String accountId = AuthUtil.getAccountID();

        if(accountId == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        ReturnRequestDTO returnRequestDTO = returnRequestService.getReturnRequestByID(id, accountId);

         return ResponseEntity.ok(returnRequestDTO);
    }
}
