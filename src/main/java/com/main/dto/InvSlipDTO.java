package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvSlipDTO {
    private String id; //tạo ở BE nên ở đây null
    private Boolean type; // true -> Xuất/ false -> nhập
    private String from; //facilityId from
    private String fromName;
    private String to; //facilityId to
    private String toName;
    private String status; // Pending/Approved/Rejected/Done
    private String staff; //được gáng ở BE
    private String staffName; //được gáng ở BE
    private String approver; //được gáng ở BE
    private String approverName; //đươc gáng ở BE
    private LocalDateTime createDate;
    private String note;

    private List<InvSlipDetailDTO> details;

    public InvSlipDTO(String id, Boolean type, String from, String fromName, String to, String toName, String status, String staff, String staffName, String approver, String approverName, LocalDateTime createDate, String note) {
        this.id = id;
        this.type = type;
        this.from = from;
        this.fromName = fromName;
        this.to = to;
        this.toName = toName;
        this.status = status;
        this.staff = staff;
        this.staffName = staffName;
        this.approver = approver;
        this.approverName = approverName;
        this.createDate = createDate;
        this.note = note;
    }
}
