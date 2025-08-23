package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor
@AllArgsConstructor
public class LogOrderDTO {
    private LocalDateTime updateAt;
    private String content;
    private String staffName;
}
