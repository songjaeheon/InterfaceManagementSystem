package com.ims.eims.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Request DTO for creating a new notice
@Getter
@Setter
public class NoticeCreateRequestDto {
    @NotNull(message = "Title is required")
    private String title;
    
    @NotNull(message = "Content is required")
    private String content;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive = true;
    private boolean isPinned = false;

    // Getters and Setters (omitted for brevity)
}
