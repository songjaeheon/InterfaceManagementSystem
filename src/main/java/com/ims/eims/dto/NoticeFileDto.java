package com.ims.eims.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeFileDto {
    private Long id;
    private String originalFilename;
    private Long fileSize;

    public NoticeFileDto(Long id, String originalFilename, Long fileSize) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.fileSize = fileSize;
    }
}
