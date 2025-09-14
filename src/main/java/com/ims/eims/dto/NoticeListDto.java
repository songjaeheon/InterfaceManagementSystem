package com.ims.eims.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

// List DTO for notice list
@Getter
@Setter
public class NoticeListDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private boolean isPinned;

    public NoticeListDto(Long id, String title, LocalDateTime createdAt, boolean isPinned) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.isPinned = isPinned;
    }
    // Getters
}
