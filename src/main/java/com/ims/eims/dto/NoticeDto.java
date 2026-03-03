package com.ims.eims.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

// Detail DTO for single notice
@Getter
@Setter
public class NoticeDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isPinned;
    private java.util.List<NoticeFileDto> files;

    public NoticeDto(Long id, String title, String content, LocalDateTime startDate, LocalDateTime endDate, boolean isPinned, java.util.List<NoticeFileDto> files) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPinned = isPinned;
        this.files = files;
    }
    // Getters
}
