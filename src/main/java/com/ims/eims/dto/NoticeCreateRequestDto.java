package com.ims.eims.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Request DTO for creating a new notice
@Getter
@Setter
public class NoticeCreateRequestDto {
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive = true;
    private boolean isPinned = false;

    // Getters and Setters (omitted for brevity)
}
