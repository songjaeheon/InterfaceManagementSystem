package com.ims.eims.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

// Request DTO for updating an existing notice
@Getter
@Setter
public class NoticeUpdateRequestDto {
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Boolean isPinned;

    // Getters and Setters (omitted for brevity)
}