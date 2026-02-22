package com.ims.eims.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Notice REST Controller.
 *
 * <p>
 * This controller exposes our Notice data as a RESTful API.
 * Marked with {@code @RestController}, every method returns a domain object serialized to JSON.
 * We use constructor injection for the service, which is easier to test.
 * This is what powers our Vue.js frontend! Bootiful!
 * </p>
 */
@RestController
@RequestMapping("/api/notices")
public class NoticeRestController {

    private final NoticeService noticeService;

    @Autowired
    public NoticeRestController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public List<Notice> getAllNotices() {
        // Java 8 Stream API in action!
        return noticeService.findAll().stream()
                .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notice> getNotice(@PathVariable Long id) {
        Notice notice = noticeService.findById(id);
        if (notice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(notice);
    }

    // Additional endpoints for Vue interaction if needed
}
