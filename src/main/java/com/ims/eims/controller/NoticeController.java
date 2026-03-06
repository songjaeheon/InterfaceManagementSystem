package com.ims.eims.controller;

import com.ims.eims.dto.NoticeCreateRequestDto;
import com.ims.eims.dto.NoticeDto;
import com.ims.eims.dto.NoticeListDto;
import com.ims.eims.dto.NoticeUpdateRequestDto;
import com.ims.eims.entity.NoticeFile;
import com.ims.eims.service.NoticeService;

import javax.validation.Valid;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // Endpoint for creating a new notice with robust multi-file upload
    // "Look at this! We're handling multipart requests with ease.
    // Just inject the List of MultipartFiles and let Spring do the rest. Beautiful!"
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeDto> createNotice(@ModelAttribute @Valid NoticeCreateRequestDto request,
                                                  @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        NoticeDto createdNotice = noticeService.createNotice(request, files);
        return new ResponseEntity<>(createdNotice, HttpStatus.CREATED);
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
    // "And here we fetch the active notices. Thanks to our service layer caching, this is blazing fast!"
    @GetMapping
    public ResponseEntity<List<NoticeListDto>> getActiveNotices() {
        List<NoticeListDto> notices = noticeService.findActiveNotices();
        return ResponseEntity.ok(notices);
    }
    
    // Endpoint for updating a notice
    @PatchMapping("/{id}")
    public ResponseEntity<NoticeDto> updateNotice(@PathVariable Long id, @RequestBody NoticeUpdateRequestDto request) {
        try {
            NoticeDto updatedNotice = noticeService.updateNotice(id, request);
            return ResponseEntity.ok(updatedNotice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint for deleting a notice
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // "And finally, let's get those files back to the user! We use a ResponseEntity with a Resource
    // and the proper Content-Disposition header so the browser knows exactly what to do. Beautiful!"
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        Resource resource = noticeService.loadFileAsResource(fileId);
        NoticeFile noticeFile = noticeService.getNoticeFile(fileId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + noticeFile.getOriginalFilename() + "\"")
                .body(resource);
    }
}
