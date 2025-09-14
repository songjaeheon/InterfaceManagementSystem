package com.ims.eims.controller;

import com.ims.eims.dto.NoticeDto;
import com.ims.eims.dto.NoticeListDto;
import com.ims.eims.service.NoticeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // Endpoint for retrieving a single notice by ID
    @GetMapping("/{id}")
    public ResponseEntity<NoticeDto> getNotice(@PathVariable Long id) {
        try {
            NoticeDto notice = noticeService.findNoticeById(id);
            return ResponseEntity.ok(notice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint for retrieving the list of active notices
    @GetMapping
    public ResponseEntity<List<NoticeListDto>> getActiveNotices() {
        List<NoticeListDto> notices = noticeService.findActiveNotices();
        return ResponseEntity.ok(notices);
    }
}
